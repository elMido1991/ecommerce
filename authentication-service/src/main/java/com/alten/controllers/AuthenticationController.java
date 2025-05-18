package com.alten.controllers;


import com.alten.business.exceptions.KeycloakSecurityException;
import com.alten.business.services.AuthenticationService;
import com.alten.controllers.dtos.SecurityDataInDto;
import com.alten.controllers.dtos.SecurityDataOutDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.KeyException;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/token")
    public ResponseEntity<SecurityDataOutDto> authenticate(@RequestBody SecurityDataInDto securityDataInDto){
        return ResponseEntity.ok(authenticationService.authenticate(securityDataInDto));
    }


    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody SecurityDataInDto securityDataInDto) throws KeycloakSecurityException {
        authenticationService.register(securityDataInDto);
        return ResponseEntity.ok().build();
    }

}
