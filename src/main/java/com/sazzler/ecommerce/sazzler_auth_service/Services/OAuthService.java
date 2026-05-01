package com.sazzler.ecommerce.sazzler_auth_service.Services;

import com.sazzler.ecommerce.sazzler_auth_service.Model.Permission;
import com.sazzler.ecommerce.sazzler_auth_service.Model.Role;
import com.sazzler.ecommerce.sazzler_auth_service.Model.User;
import com.sazzler.ecommerce.sazzler_auth_service.Repository.RoleRepo;
import com.sazzler.ecommerce.sazzler_auth_service.Repository.UserRepo;
import com.sazzler.ecommerce.sazzler_auth_service.Security.SazzlerUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
     * Finds an existing user or creates a new one from OAuth2 provider attributes,
     * then returns a {@link SazzlerUserDetails} ready for JWT generation.
     *
     * @param provider   uppercase provider name, e.g. "GOOGLE" or "GITHUB"
     * @param providerId the provider's unique user ID
     * @param email      the user's email from the provider
     * @param firstName  first name from the provider profile
     * @param lastName   last name from the provider profile
     * @return populated {@link SazzlerUserDetails}
     */
    public SazzlerUserDetails processOAuthUser(String provider, String providerId,
                                               String email, String firstName, String lastName) {
        // 1. Try to find by email first (covers account-linking scenario)
        User user = userRepo.findByEmail(email);

        if (user == null) {
            // 2. Try to find by provider + providerId (email may have changed)
            user = userRepo.findByProviderAndProviderId(provider, providerId);
        }

        if (user == null) {
            // 3. Create a new OAuth user
            user = createOAuthUser(provider, providerId, email, firstName, lastName);
        } else if (user.getProviderId() == null) {
            // 4. Link an existing local account to this OAuth provider
            user.setProvider(provider);
            user.setProviderId(providerId);
            userRepo.saveAndFlush(user);
        }

        return buildUserDetails(user);
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
                .createdAt(LocalDate.now())
                .role(role)
                .build();

        return userRepo.saveAndFlush(newUser);
    }

    private String generateUniqueUsername(String email) {
        String base = email.contains("@") ? email.substring(0, email.indexOf('@')) : email;
        // Sanitize: keep only alphanumeric and underscores
        base = base.replaceAll("[^a-zA-Z0-9_]", "_");
        String candidate = base;
        // Append a short random suffix until the username is unique
        while (userRepo.findByUsername(candidate) != null) {
            candidate = base + "_" + UUID.randomUUID().toString().substring(0, 6);
        }
        return candidate;
    }

    private SazzlerUserDetails buildUserDetails(User user) {
        Role role = user.getRole();
        Set<GrantedAuthority> authorities = role.getPermissions().stream()
                .map(Permission::getPermissionName)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
        authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));

        return new SazzlerUserDetails(
                user.getUserId(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                authorities
        );
    }
}
