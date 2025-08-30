package com.sazzler.ecommerce.sazzler_auth_service.Repository;

import com.sazzler.ecommerce.sazzler_auth_service.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends JpaRepository<User,Integer> {
    <Optional> User findByUsername(String username);
    <Optional> User findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

}
