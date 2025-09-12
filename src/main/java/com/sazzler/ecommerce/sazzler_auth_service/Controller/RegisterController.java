package com.sazzler.ecommerce.sazzler_auth_service.Controller;

import com.sazzler.ecommerce.api_def.auth_service.DTO.UserRegReq;
import com.sazzler.ecommerce.api_def.auth_service.DTO.UserRegResponse;
import com.sazzler.ecommerce.sazzler_auth_service.Services.RegisterService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
public class RegisterController {

    RegisterService registerService;

    @Autowired
    public RegisterController(RegisterService registerService) {
        this.registerService = registerService;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }
//controllers
    @PostMapping( value = "/Register")
    public ResponseEntity<String> registerReq(@Valid @RequestBody UserRegReq regReq) {

        return registerService.register(regReq);}

}
