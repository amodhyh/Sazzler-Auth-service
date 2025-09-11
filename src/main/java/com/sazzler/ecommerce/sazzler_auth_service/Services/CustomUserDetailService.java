package com.sazzler.ecommerce.sazzler_auth_service.Services;

import com.sazzler.ecommerce.sazzler_auth_service.Model.Permission;
import com.sazzler.ecommerce.sazzler_auth_service.Model.Role;
import com.sazzler.ecommerce.sazzler_auth_service.Model.User;
import com.sazzler.ecommerce.sazzler_auth_service.Repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Permissions;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

//
@Service
public class CustomUserDetailService implements UserDetailsService {


    private final UserRepo userRepo;

    public CustomUserDetailService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
        User user = id.contains("@") ? userRepo.findByEmail(id) : userRepo.findByUsername(id);
        if (user == null) {
            throw new UsernameNotFoundException("No user found with id: " + id);
        }
        Role role=user.getRole();
        Set<Permission> permissions=role.getPermissions();

        Set<GrantedAuthority> authorities = permissions.stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermissionName())).collect(Collectors.toSet());

        SimpleGrantedAuthority roleAuth = new SimpleGrantedAuthority("ROLE_" + user.getRole().getName());
        authorities.add(roleAuth);

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                authorities
        );
        //spring sec automatically adds this user details to the security context when creating
        //this user object
    }


}




