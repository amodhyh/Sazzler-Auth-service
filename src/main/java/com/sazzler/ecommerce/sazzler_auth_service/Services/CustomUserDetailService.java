package com.sazzler.ecommerce.sazzler_auth_service.Services;

import com.sazzler.ecommerce.sazzler_auth_service.Model.Permission;
import com.sazzler.ecommerce.sazzler_auth_service.Model.Role;
import com.sazzler.ecommerce.sazzler_auth_service.Model.User;
import com.sazzler.ecommerce.sazzler_auth_service.Repository.UserRepo;
import com.sazzler.ecommerce.sazzler_auth_service.Security.SazzlerUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepo userRepo;

    @Autowired
    public CustomUserDetailService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public SazzlerUserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
        //id can be either email or username
        //check if the id contains '@' to determine if it's an email
        User user = id.contains("@") ? userRepo.findByEmail(id) : userRepo.findByUsername(id);

        if (user == null) {
            throw new UsernameNotFoundException("No user found with str_id: " + id);
        }
        Role role=user.getRole();
        Set<Permission> permissions=role.getPermissions();

        //store the permissions in the set of granted authorities
        Set<GrantedAuthority> authorities = permissions.stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermissionName())).collect(Collectors.toSet());

        //store the role in the set of granted authorities
        SimpleGrantedAuthority roleAuth = new SimpleGrantedAuthority("ROLE_" + user.getRole().getName());
        authorities.add(roleAuth);
        //authorities = {"ROLE_ADMIN","PERM_READ","PERM_WRITE"}
        return new SazzlerUserDetails(
                user.getUserId(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                authorities
        );
        //spring sec automatically adds this user details to the security context when creating
        //this user object
    }


}
