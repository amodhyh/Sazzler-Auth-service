package com.sazzler.ecommerce.sazzler_auth_service.Services;

import com.sazzler.ecommerce.sazzler_api_def.auth_service.Exceptions.UserNotFoundException;
import com.sazzler.ecommerce.sazzler_auth_service.Model.Permission;
import com.sazzler.ecommerce.sazzler_auth_service.Model.Role;
import com.sazzler.ecommerce.sazzler_auth_service.Model.User;
import com.sazzler.ecommerce.sazzler_auth_service.Repository.RoleRepo;
import com.sazzler.ecommerce.sazzler_auth_service.Repository.UserRepo;
import com.sazzler.ecommerce.sazzler_auth_service.Security.SazzlerUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OAuthService {

    private final UserRepo userRepo;
    private final RoleRepo roleRepo;

    @Autowired
    public OAuthService(UserRepo userRepo, RoleRepo roleRepo) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
    }

    /**
     * Finds an existing user or creates a new one from OAuth2 provider attributes.
     * Implements "Account Linking" and "Early Protection" strategies.
     */
    @Transactional // Ensures atomicity for account linking and user creation
    public SazzlerUserDetails processOAuthUser(String provider, String providerId,
                                               String email, String firstName, String lastName) {
        // Use functional methods (.map, .orElseGet) to handle the Optional logic flow
        return userRepo.findByEmail(email)
                .map(existingUser -> handleAccountLinking(existingUser, provider, providerId))
                .orElseGet(() ->
                    // If no email match, try finding by specific provider ID
                    userRepo.findByProviderAndProviderId(provider, providerId)
                            .map(this::buildUserDetails)
                            // If still no match, provision a new user [cite: 172]
                            .orElseGet(() -> buildUserDetails(createOAuthUser(provider, providerId, email, firstName, lastName)))
                );
    }

    private SazzlerUserDetails handleAccountLinking(User user, String provider, String providerId) {
        // Case 1: Existing local account with no provider linked [cite: 170]
        if (user.getProvider() == null) {
            user.setProvider(provider);
            user.setProviderId(providerId);
            return buildUserDetails(userRepo.saveAndFlush(user));
        }

        // Case 2: Matching OAuth account
        if (provider.equals(user.getProvider()) && providerId.equals(user.getProviderId())) {
            return buildUserDetails(user);
        }

        // Case 3: Email exists but is linked to a different provider (Guardrail)
        throw new UserNotFoundException("Email is already associated with a different provider account.");
    }

    private User createOAuthUser(String provider, String providerId,
                                 String email, String firstName, String lastName) {
        Role role = roleRepo.findByName("ROLE_USER");
        String username = generateUniqueUsername(email);

        User newUser = User.builder()
                .email(email)
                .username(username)
                .firstName(firstName != null ? firstName : "")
                .lastName(lastName != null ? lastName : "")
                .provider(provider)
                .providerId(providerId)
                .role(role)
                .build();

        try {
            return userRepo.saveAndFlush(newUser);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            // Handle rare race condition where the generated username was taken between check and save
            newUser.setUsername(generateUniqueUsername(email));
            return userRepo.saveAndFlush(newUser);
        }
    }

    private String generateUniqueUsername(String email) {
        String base = email.contains("@") ? email.substring(0, email.indexOf('@')) : email;
        base = base.replaceAll("[^a-zA-Z0-9_]", "_");

        // Immediately append a short hash to ensure uniqueness with high probability in one shot [cite: 110, 149, 155]
        return base + "_" + UUID.randomUUID().toString().substring(0, 8);
    }

    private SazzlerUserDetails buildUserDetails(User user) {
        Role role = user.getRole();
        Set<GrantedAuthority> authorities = role.getPermissions().stream()
                .map(Permission::getPermissionName)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());

        authorities.add(new SimpleGrantedAuthority(role.getName()));

        // Populate the security context encapsulation [cite: 77, 175]
        return new SazzlerUserDetails(
                user.getUserId(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                authorities
        );
    }
}