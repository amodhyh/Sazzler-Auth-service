package com.sazzler.ecommerce.sazzler_auth_service.Services;

import com.sazzler.ecommerce.api_def.auth_service.DTO.UserRegReq;
import com.sazzler.ecommerce.api_def.auth_service.Exceptions.EmptyRegistrationDetails;
import com.sazzler.ecommerce.api_def.auth_service.Exceptions.UserAlreadyExists;
import com.sazzler.ecommerce.api_def.auth_service.Exceptions.UserTooYound;
import com.sazzler.ecommerce.sazzler_auth_service.Model.Role;
import com.sazzler.ecommerce.sazzler_auth_service.Model.User;
import com.sazzler.ecommerce.sazzler_auth_service.Repository.RoleRepo;
import com.sazzler.ecommerce.sazzler_auth_service.Repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;

@Service
public class RegisterService {
    private final DelegatingPasswordEncoder passwordEncoder;
    private final UserRepo userRepo;
    private final RoleRepo roleRepo;

    @Autowired
    public RegisterService(UserRepo userRepo, RoleRepo roleRepo, DelegatingPasswordEncoder passwordEncoder){
        this.userRepo=userRepo;
        this.roleRepo=roleRepo;
        this.passwordEncoder = passwordEncoder;
    }

    public ResponseEntity<String> register(UserRegReq userRegReq) {

        if (userRegReq == null ||
            userRegReq.username() == null || userRegReq.username().isBlank() ||
            userRegReq.email() == null || userRegReq.email().isBlank() ||
            userRegReq.password() == null || userRegReq.password().isBlank() ||
            userRegReq.firstName() == null || userRegReq.firstName().isBlank() ||
            userRegReq.lastName() == null || userRegReq.lastName().isBlank() ||
            userRegReq.dob() == null) {
            throw new EmptyRegistrationDetails("Registration request contains invalid data. All fields are required.");
        }

        if(userRepo.existsByUsernameandEmail(userRegReq.email(), userRegReq.username())) {
              throw new UserAlreadyExists("User already exists");
        }
        else if (Period.between(userRegReq.dob(), LocalDate.now()).getYears()<16) {
            throw new UserTooYound("User must be at least 16 years old to register.");

        }

        String password=passwordEncoder.encode( userRegReq.password());
        Role role=null;
        if(userRegReq.role().equals("ROLE_ADMIN")){
            role=roleRepo.findByName("ROLE_ADMIN");
        }
        else{
            role=roleRepo.findByName("ROLE_USER");
        }

        userRepo.saveAndFlush(User.builder()
                .username(userRegReq.username())
                .email(userRegReq.email())
                .firstName(userRegReq.firstName())
                .lastName(userRegReq.lastName())
                .dob(userRegReq.dob())
                .role(role)
                .createdAt(LocalDate.now())
                .password(password)
                .build());

        return  ResponseEntity.status(200).body("User registered successfully");


    }


}
