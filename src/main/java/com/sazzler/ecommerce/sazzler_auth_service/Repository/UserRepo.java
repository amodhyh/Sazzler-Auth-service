package com.sazzler.ecommerce.sazzler_auth_service.Repository;

import com.sazzler.ecommerce.sazzler_auth_service.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends JpaRepository<User,Integer> {
    <Optional> User findByUsername(String username);
    <Optional> User findByEmail(String email);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.username = ?1 OR u.email = ?2")
    boolean existsByUsernameandEmail(String username,String email);

}
