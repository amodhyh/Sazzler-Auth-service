package com.sazzler.ecommerce.sazzler_auth_service.DTO;
import lombok.Data;

import java.util.Date;

@Data
public class UserRegReq {
String username;
String password;
String email;
String firstName;
String lastName;
Date dob;
Date createdAt;

}
