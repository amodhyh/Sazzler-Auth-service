package com.sazzler.ecommerce.sazzler_auth_service.Services;

import com.sazzler.ecommerce.sazzler_auth_service.Model.Permission;
import com.sazzler.ecommerce.sazzler_auth_service.Model.Role;
import com.sazzler.ecommerce.sazzler_auth_service.Model.User;
import com.sazzler.ecommerce.sazzler_auth_service.Repository.RoleRepo;
import com.sazzler.ecommerce.sazzler_auth_service.Repository.UserRepo;
import com.sazzler.ecommerce.sazzler_auth_service.Security.SazzlerUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class OAuthServiceTest {

    @Mock
    private UserRepo userRepo;

    @Mock
    private RoleRepo roleRepo;

    private OAuthService oAuthService;

    private Role defaultRole;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        oAuthService = new OAuthService(userRepo, roleRepo);

        Set<Permission> permissions = new HashSet<>();
        Permission perm = new Permission();
        perm.setPermissionName("READ");
        permissions.add(perm);

        defaultRole = new Role();
        defaultRole.setName("ROLE_USER");
        // inject permissions via reflection since Role.permissions has no setter
        try {
            var field = Role.class.getDeclaredField("permissions");
            field.setAccessible(true);
            field.set(defaultRole, permissions);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void processOAuthUser_newUser_createsAndReturnsDetails() {
        when(userRepo.findByEmail("alice@example.com")).thenReturn(Optional.empty());
        when(userRepo.findByProviderAndProviderId("GOOGLE", "g123")).thenReturn(Optional.empty());
        when(roleRepo.findByName("ROLE_USER")).thenReturn(defaultRole);
        when(userRepo.saveAndFlush(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            // simulate generated ID
            try {
                var f = User.class.getDeclaredField("userId");
                f.setAccessible(true);
                f.set(u, 1L);
            } catch (Exception e) { throw new RuntimeException(e); }
            return u;
        });

        SazzlerUserDetails details = oAuthService.processOAuthUser("GOOGLE", "g123", "alice@example.com", "Alice", "Smith");

        assertNotNull(details);
        assertEquals("alice@example.com", details.getEmail());
        assertTrue(details.getUsername().startsWith("alice_"), "Username should start with 'alice_': " + details.getUsername());
        assertEquals(14, details.getUsername().length(), "Username should have length 14 (alice_ + 8 chars)");

        assertTrue(details.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
        verify(userRepo).saveAndFlush(any(User.class));
    }

    @Test
    public void processOAuthUser_existingUserByEmail_doesNotCreate() {
        User existingUser = buildUser(42L, "alice", "alice@example.com", "GOOGLE", "g123");
        when(userRepo.findByEmail("alice@example.com")).thenReturn(Optional.of(existingUser));

        SazzlerUserDetails details = oAuthService.processOAuthUser("GOOGLE", "g123", "alice@example.com", "Alice", "Smith");

        assertNotNull(details);
        assertEquals(42L, details.getId());
        verify(userRepo, never()).saveAndFlush(any());
    }

    @Test
    public void processOAuthUser_existingUserByProvider_doesNotCreate() {
        User existingUser = buildUser(7L, "bob", "bob@example.com", "GITHUB", "gh99");
        when(userRepo.findByEmail("bob@example.com")).thenReturn(Optional.empty());
        when(userRepo.findByProviderAndProviderId("GITHUB", "gh99")).thenReturn(Optional.of(existingUser));

        SazzlerUserDetails details = oAuthService.processOAuthUser("GITHUB", "gh99", "bob@example.com", "Bob", "Jones");

        assertNotNull(details);
        assertEquals(7L, details.getId());
        verify(userRepo, never()).saveAndFlush(any());
    }

    @Test
    public void processOAuthUser_localUserLinkedToOAuth_savesProviderInfo() {
        // A user that exists by email but has no provider yet (local account)
        User localUser = buildUser(5L, "carol", "carol@example.com", null, null);
        when(userRepo.findByEmail("carol@example.com")).thenReturn(Optional.of(localUser));
        when(userRepo.saveAndFlush(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        oAuthService.processOAuthUser("GOOGLE", "g456", "carol@example.com", "Carol", "White");

        assertEquals("GOOGLE", localUser.getProvider());
        assertEquals("g456", localUser.getProviderId());
        verify(userRepo).saveAndFlush(localUser);
    }

    @Test
    public void processOAuthUser_dataIntegrityViolation_retriesWithNewUsername() {
        when(userRepo.findByEmail("conflict@example.com")).thenReturn(Optional.empty());
        when(userRepo.findByProviderAndProviderId(any(), any())).thenReturn(Optional.empty());
        when(roleRepo.findByName("ROLE_USER")).thenReturn(defaultRole);

        when(userRepo.saveAndFlush(any(User.class)))
                .thenThrow(new DataIntegrityViolationException("Username conflict"))
                .thenAnswer(inv -> {
                    User u = inv.getArgument(0);
                    try {
                        var f = User.class.getDeclaredField("userId");
                        f.setAccessible(true);
                        f.set(u, 100L);
                    } catch (Exception e) { throw new RuntimeException(e); }
                    return u;
                });

        SazzlerUserDetails details = oAuthService.processOAuthUser("GOOGLE", "g111", "conflict@example.com", "Conflict", "User");

        assertNotNull(details);
        assertEquals(100L, details.getId());
        assertTrue(details.getUsername().startsWith("conflict_"));
        verify(userRepo, times(2)).saveAndFlush(any(User.class));
    }

    // --- helpers ---

    private User buildUser(Long id, String username, String email, String provider, String providerId) {
        User u = User.builder()
                .username(username)
                .email(email)
                .firstName("Test")
                .lastName("User")
                .provider(provider)
                .providerId(providerId)
                .role(defaultRole)
                .build();
        try {
            var f = User.class.getDeclaredField("userId");
            f.setAccessible(true);
            f.set(u, id);
        } catch (Exception e) { throw new RuntimeException(e); }
        return u;
    }
}
