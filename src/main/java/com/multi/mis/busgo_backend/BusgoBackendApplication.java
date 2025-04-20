package com.multi.mis.busgo_backend;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.multi.mis.busgo_backend.model.User;
import com.multi.mis.busgo_backend.service.BusCompanyService;
import com.multi.mis.busgo_backend.service.UserService;

@SpringBootApplication
public class BusgoBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BusgoBackendApplication.class, args);
    }

    @Bean
    CommandLineRunner commandLineRunner(
            UserService userService,
            BusCompanyService busCompanyService,
            PasswordEncoder passwordEncoder) {
        return args -> {
            // Initialize default users and bus companies here if needed
            // Example:
            // userService.createUser(new User("admin", "
            //add admin with initialization
            // public User(String username, String password, String email, String firstName, String lastName, String phoneNumber, String role) {
            //     this.username = username;
            //     this.password = password;
            //     this.email = email;
            //     this.firstName = firstName;
            //     this.lastName = lastName;
            //     this.phoneNumber = phoneNumber;
            //     this.role = role;
            // }
           userService.createUser(new User("admin", passwordEncoder.encode("admin"), "admin@example", "Admin", "User", "123456789", "ADMIN"));
         


};
            };
        };

