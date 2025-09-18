package com.sazzler.ecommerce.sazzler_auth_service.Model;


import jakarta.persistence.*;
import jakarta.validation.Constraint;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Getter @Setter
@Table(name="users") // Use quoted lowercase to be explicit
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    // The SequenceGenerator might not be needed with IDENTITY strategy for Postgres
    // @SequenceGenerator(name = "id_seq", sequenceName = "user_id_seq", allocationSize = 1)
    @Column(name = "id") // Change "USER_ID" to the actual column name, likely "id"
    private Long userId;

    @Email(message = "Email should be valid")
    @NotNull(message = "Email cannot be null")
    @NotBlank(message = "Email cannot be blank")
    @Column(name = "email", unique = true)
    private String email;

    @NotNull(message = "Date of Birth cannot be null")
    @Column(name = "date_of_birth")
    private   LocalDate dob;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDate createdAt;



    @NotNull(message = "Password cannot be null")
    @NotBlank(message = "Password cannot be blank")
    @Column(name = "password" , columnDefinition = "TEXT")
    private String password;


    @NotNull(message = "Username cannot be null")
    @NotBlank(message = "Username cannot be blank")
    @Column(name = "user_name", unique = true)
    private String username;

    @NotNull(message = "First name cannot be null")
    @NotBlank(message = "First name cannot be blank")
    @Column(name = "first_name")
    private String firstName;

    @NotNull(message = "Last name cannot be null")
    @NotBlank(message = "Last name cannot be blank")
    @Column(name = "last_name")
    private String lastName;

    @Getter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    Role role;
}
