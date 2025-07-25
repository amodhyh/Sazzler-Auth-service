package com.sazzler.ecommerce.sazzler_auth_service.Repository;

import com.sazzler.ecommerce.sazzler_auth_service.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<User,Integer> {
}
