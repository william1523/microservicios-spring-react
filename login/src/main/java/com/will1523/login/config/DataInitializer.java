package com.will1523.login.config;

import com.will1523.login.model.Role;
import com.will1523.login.model.User;
import com.will1523.login.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.count() == 0) {
                User superAdmin = new User();
                superAdmin.setUsername("superadmin");
                superAdmin.setEmail("super@admin.com");
                superAdmin.setPassword(passwordEncoder.encode("superadmin123"));
                superAdmin.setRole(Role.SUPER_ADMIN);
                superAdmin.setCompanyCode("GLOBAL");

                userRepository.save(superAdmin);
                System.out.println("Super Admin user created: superadmin / superadmin123");
                User admin = new User();
                admin.setUsername("admin");
                admin.setEmail("admin@admin.com");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRole(Role.ADMIN);
                admin.setCompanyCode("1");

                userRepository.save(admin);
                System.out.println(" Admin user created: admin / admin123");

            }
        };
    }
}
