package com.sazzler.ecommerce.sazzler_auth_service.DTO;

import com.sazzler.ecommerce.sazzler_auth_service.Model.Role;

import java.util.Set;

public record UserAuthInforDTO(String id, Role role, Set<String> permissions) {

}
