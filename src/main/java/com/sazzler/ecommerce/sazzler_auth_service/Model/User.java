package com.sazzler.ecommerce.sazzler_auth_service.Model;


import jakarta.persistence.*;
import jakarta.validation.Constraint;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Getter @Setter
@Table(name="USER")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @SequenceGenerator(name = "id_seq", sequenceName = "USER_ID_SEQ", allocationSize = 1)
    @Column(name = "USER_ID", unique = true)
    private Long userId;

    @Email(message = "Email should be valid")
    @NotNull(message = "Email cannot be null")
    @NotBlank(message = "Email cannot be blank")
    @Column(name = "EMAIL", unique = true)
    private String email;

    @NotNull(message = "Date of Birth cannot be null")
    @Column(name = "DATE_OF_BIRTH")
    private   Date dob;

    @Column(name = "CREATED_AT")
    private LocalDate createdAt;

    @NotNull(message = "Password cannot be null")
    @NotBlank(message = "Password cannot be blank")

    @Column(name = "PASSWORD")
    private String password;

    @Id
    @NotNull(message = "Username cannot be null")
    @NotBlank(message = "Username cannot be blank")
    @Column(name = "USER_NAME", unique = true)
    private String username;

    @NotNull(message = "First name cannot be null")
    @NotBlank(message = "First name cannot be blank")
    @Column(name = "FIRST_NAME")
    private String firstName;

    @NotNull(message = "Last name cannot be null")
    @NotBlank(message = "Last name cannot be blank")
    @Column(name = "LAST_NAME")
    private String lastName;

    @Getter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ROLE_ID")
    Role role;




}

