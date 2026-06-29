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
    @Transactional
    public SazzlerUserDetails processOAuthUser(String provider, String providerId,
                                               String email, String firstName, String lastName) {
        return userRepo.findByEmail(email)
                .map(existingUser -> handleAccountLinking(existingUser, provider, providerId))
                .orElseGet(() ->
                    userRepo.findByProviderAndProviderId(provider, providerId)
                            .map(this::buildUserDetails)
                            .orElseGet(() -> buildUserDetails(createOAuthUser(provider, providerId, email, firstName, lastName)))
                );
    }

    private SazzlerUserDetails handleAccountLinking(User user, String provider, String providerId) {
        if (user.getProvider() == null) {
            user.setProvider(provider);
            user.setProviderId(providerId);
            return buildUserDetails(userRepo.saveAndFlush(user));
        }

        if (provider.equals(user.getProvider()) && providerId.equals(user.getProviderId())) {
            return buildUserDetails(user);
        }

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
            newUser.setUsername(generateUniqueUsername(email));
            return userRepo.saveAndFlush(newUser);
        }
    }

    private String generateUniqueUsername(String email) {
        String base = email.contains("@") ? email.substring(0, email.indexOf('@')) : email;
        base = base.replaceAll("[^a-zA-Z0-9_]", "_");
        return base + "_" + UUID.randomUUID().toString().substring(0, 8);
    }

    private SazzlerUserDetails buildUserDetails(User user) {
        Role role = user.getRole();
        Set<GrantedAuthority> authorities = role.getPermissions().stream()
                .map(Permission::getPermissionName)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
        authorities.add(new SimpleGrantedAuthority(role.getName()));

        return new SazzlerUserDetails(
                user.getUserId(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                authorities
        );
    }
}
