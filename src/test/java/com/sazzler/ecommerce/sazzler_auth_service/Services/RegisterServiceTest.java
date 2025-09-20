package com.sazzler.ecommerce.sazzler_auth_service.Services;

import com.sazzler.ecommerce.api_def.auth_service.DTO.UserRegReq;
import com.sazzler.ecommerce.api_def.auth_service.Exceptions.EmptyRegistrationDetails;
import com.sazzler.ecommerce.api_def.auth_service.Exceptions.UserAlreadyExists;
import com.sazzler.ecommerce.api_def.auth_service.Exceptions.UserTooYound;
import com.sazzler.ecommerce.sazzler_auth_service.Model.Role;
import com.sazzler.ecommerce.sazzler_auth_service.Model.User;
import com.sazzler.ecommerce.sazzler_auth_service.Repository.RoleRepo;
import com.sazzler.ecommerce.sazzler_auth_service.Repository.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class RegisterServiceTest {
//dependencies are mocked
    @Mock
    private UserRepo userRepo;

    @Mock
    private RoleRepo roleRepo;

    @Mock
    private DelegatingPasswordEncoder passwordEncoder;

    private RegisterService registerService;
//each test start with a fresh Register Service instance
    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        registerService = new RegisterService(userRepo, roleRepo, passwordEncoder);
    }

    @Test
    public void register_success_createsUser() {
        String username = "testuser";
        String email = "test@example.com";
        String password = "password";
        UserRegReq req = new UserRegReq(username, password, email, "First", "Last", LocalDate.of(2000,1,1), "ROLE_USER");

        when(userRepo.existsByUsernameandEmail(email, username)).thenReturn(false);
        Role role = new Role();
        role.setName("ROLE_USER");
        when(roleRepo.findByName("ROLE_USER")).thenReturn(role);
        when(passwordEncoder.encode(password)).thenReturn("encoded-pass");
        when(userRepo.saveAndFlush(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ResponseEntity<String> resp = registerService.register(req);

        assertEquals(200, resp.getStatusCode().value());
        assertEquals("User registered successfully", resp.getBody());
        verify(userRepo, times(1)).saveAndFlush(any(User.class));
    }

    @Test
    public void register_adminRole_usesAdminRole() {
        String username = "admin";
        String email = "admin@example.com";
        String password = "password";
        UserRegReq req = new UserRegReq(username, password, email, "Admin", "User", LocalDate.of(1990,1,1), "ROLE_ADMIN");

        when(userRepo.existsByUsernameandEmail(email, username)).thenReturn(false);
        Role adminRole = new Role();
        adminRole.setName("ROLE_ADMIN");
        when(roleRepo.findByName("ROLE_ADMIN")).thenReturn(adminRole);
        when(passwordEncoder.encode(password)).thenReturn("encoded-admin");
        when(userRepo.saveAndFlush(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ResponseEntity<String> resp = registerService.register(req);

        assertEquals(200, resp.getStatusCode().value());
        assertEquals("User registered successfully", resp.getBody());
        verify(userRepo).saveAndFlush(argThat(u -> ((User)u).getRole().getName().equals("ROLE_ADMIN")));
    }

    @Test
    public void register_whenUserAlreadyExists_throws() {
        UserRegReq req = new UserRegReq("testuser", "password", "test@example.com", "First", "Last", LocalDate.of(2000,1,1), "ROLE_USER");
        when(userRepo.existsByUsernameandEmail(req.email(), req.username())).thenReturn(true);

        assertThrows(UserAlreadyExists.class, () -> registerService.register(req));
        verify(userRepo, never()).saveAndFlush(any());
    }

    @Test
    public void register_tooYoung_throws() {
        UserRegReq req = new UserRegReq("young", "password", "young@example.com", "First", "Last", LocalDate.now().minusYears(15), "ROLE_USER");
        when(userRepo.existsByUsernameandEmail(req.email(), req.username())).thenReturn(false);

        assertThrows(UserTooYound.class, () -> registerService.register(req));
        verify(userRepo, never()).saveAndFlush(any());
    }

    @Test
    public void register_emptyDetails_throws() {
        UserRegReq req1 = new UserRegReq("", "password", "email@example.com", "First", "Last", LocalDate.of(2000,1,1), "ROLE_USER");
        UserRegReq req2 = new UserRegReq("user", "", "email@example.com", "First", "Last", LocalDate.of(2000,1,1), "ROLE_USER");
        UserRegReq req3 = new UserRegReq(null, null, null, null, null, null, null);

        assertThrows(EmptyRegistrationDetails.class, () -> registerService.register(req1));
        assertThrows(EmptyRegistrationDetails.class, () -> registerService.register(req2));
        assertThrows(EmptyRegistrationDetails.class, () -> registerService.register(req3));
        verify(userRepo, never()).saveAndFlush(any());
    }
}

