package com.will1523.login.controller;

import com.will1523.login.dto.AuthRequest;
import com.will1523.login.dto.AuthResponse;
import com.will1523.login.service.AuthService;
import com.will1523.login.service.EncryptionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final EncryptionService encryptionService;

    public AuthController(AuthService authService, EncryptionService encryptionService) {
        this.authService = authService;
        this.encryptionService = encryptionService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        try {
            return ResponseEntity.ok(authService.login(request));
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).build();
        }
    }

    @org.springframework.web.bind.annotation.GetMapping("/public-key")
    public ResponseEntity<String> getPublicKey() {
        return ResponseEntity.ok(encryptionService.getPublicKeyBase64());
    }
}
