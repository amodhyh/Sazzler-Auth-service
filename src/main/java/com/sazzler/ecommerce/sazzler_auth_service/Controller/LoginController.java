package com.sazzler.ecommerce.sazzler_auth_service.Controller;

import com.sazzler.ecommerce.api_def.auth_service.DTO.UserLogReq;
import com.sazzler.ecommerce.api_def.auth_service.DTO.UserLogResponse;
import com.sazzler.ecommerce.sazzler_auth_service.DTO.UserLogReq;
import com.sazzler.ecommerce.sazzler_auth_service.DTO.UserLogResponse;
import com.sazzler.ecommerce.sazzler_auth_service.Services.LoginService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
public class LoginController {

LoginService loginService;
@Autowired
LoginController(LoginService loginService){
    this.loginService=loginService;
}

    @PostMapping("/login")
    public ResponseEntity<UserLogResponse> login(@RequestBody UserLogReq userLogReq) {

        return loginService.authenticate(new UserLogResponse("Login Successful","kk"));
    }

//        @PostMapping("/login")
//        public ResponseEntity<?> authenticate(UserLogReq userLogReq) {
//            Authentication authentication = authenticationManager.authenticate(
//                    new UsernamePasswordAuthenticationToken(userLogReq.getUsername(), userLogReq.getPassword()));
//        }
    }