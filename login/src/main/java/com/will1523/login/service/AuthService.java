package com.will1523.login.service;

import com.will1523.login.dto.AuthRequest;
import com.will1523.login.dto.AuthResponse;
import com.will1523.login.security.JwtUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(AuthService.class);

    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;
    private final com.will1523.login.repository.UserRepository userRepository;
    private final com.will1523.login.service.SessionService sessionService;
    private final EncryptionService encryptionService;

    public AuthService(JwtUtils jwtUtils, PasswordEncoder passwordEncoder, com.will1523.login.repository.UserRepository userRepository, com.will1523.login.service.SessionService sessionService, EncryptionService encryptionService) {
        this.jwtUtils = jwtUtils;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.sessionService = sessionService;
        this.encryptionService = encryptionService;
    }

    public AuthResponse login(AuthRequest request) {
        String decryptedUsername = encryptionService.decrypt(request.getUsername());
        String decryptedPassword = encryptionService.decrypt(request.getPassword());

        logger.info("Attempting login for user: {}", decryptedUsername);
        
        com.will1523.login.model.User user = userRepository.findByUsername(decryptedUsername)
                .orElseThrow(() -> {
                    logger.warn("Login failed: User not found for username: {}", decryptedUsername);
                    return new RuntimeException("Invalid credentials");
                });

        if (!passwordEncoder.matches(decryptedPassword, user.getPassword())) {
            logger.warn("Login failed: Invalid password for user: {}", decryptedUsername);
            throw new RuntimeException("Invalid credentials");
        }

        logger.info("Password verification successful for user: {}", decryptedUsername);

        String token = jwtUtils.generateToken(user);
        logger.info("JWT token generated successfully for user: {}", decryptedUsername);

        sessionService.saveSession(user.getUsername(), token);
        logger.info("Session stored in Redis for user: {}", decryptedUsername);

        return new AuthResponse(token);
    }
}
