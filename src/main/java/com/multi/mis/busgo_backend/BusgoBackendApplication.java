package com.multi.mis.busgo_backend;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.multi.mis.busgo_backend.model.User;
import com.multi.mis.busgo_backend.service.BusCompanyService;
import com.multi.mis.busgo_backend.service.UserService;

@SpringBootApplication
@EnableScheduling
public class BusgoBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BusgoBackendApplication.class, args);
    }


}

