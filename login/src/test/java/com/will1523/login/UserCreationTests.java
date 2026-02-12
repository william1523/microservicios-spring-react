package com.will1523.login;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.will1523.login.dto.UserRequest;
import com.will1523.login.model.Role;
import com.will1523.login.model.User;
import com.will1523.login.repository.UserRepository;
import com.will1523.login.security.JwtUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserCreationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @MockBean
    private com.will1523.login.service.SessionService sessionService;

    @Test
    void testUserCreationFlow() throws Exception {
        // 1. SuperAdmin creates an Admin
        User superAdmin = new User("superadmin", "super@test.com", "superadmin123", Role.SUPER_ADMIN, "AdminCorp");
        String superAdminToken = jwtUtils.generateToken(superAdmin);

        UserRequest createAdminRequest = new UserRequest("adminUser1", "admin@test.com", "password1", Role.ADMIN, "CompanyA");
        
        when(userRepository.findByUsername("adminUser")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(i -> {
            User u = i.getArgument(0);
            u.setId("1"); // Mock ID generation
            return u;
        });

        mockMvc.perform(post("/users")
                .header("Authorization", "Bearer " + superAdminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createAdminRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("ADMIN"));

        // 2. Admin creates a User (verifying the newly created admin can do it)
        // In a real e2e we would use the created user credentials, but here we just generate a token for the Admin we 'intended' to create.
        User adminUser = new User("adminUser", "admin@test.com", "pass", Role.ADMIN, "CompanyA");
        String adminToken = jwtUtils.generateToken(adminUser);

        UserRequest createUserRequest = new UserRequest("normalUser", "user@test.com", "password", Role.USER, "CompanyA");

        when(userRepository.findByUsername("normalUser")).thenReturn(Optional.empty());
        
        mockMvc.perform(post("/users")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createUserRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    void testUserCannotCreateUser() throws Exception {
        User normalUser = new User("user", "user@test.com", "pass", Role.USER, "CompanyA");
        String userToken = jwtUtils.generateToken(normalUser);

        UserRequest request = new UserRequest("anotherUser", "other@test.com", "password", Role.USER, "CompanyA");

        mockMvc.perform(post("/users")
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
                
    }
}
