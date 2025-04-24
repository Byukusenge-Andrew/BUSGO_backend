//package com.multi.mis.busgo_backend.security;
//
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Service;
//
//import java.util.Collections;
//import java.util.logging.Logger;
//
//import com.multi.mis.busgo_backend.model.User;
//import com.multi.mis.busgo_backend.service.UserService;
//
//@Service
//public class CustomUserDetailsService implements UserDetailsService {
//    private static final Logger logger = Logger.getLogger(CustomUserDetailsService.class.getName());
//    private final UserService userService;
//
//    public CustomUserDetailsService(UserService userService) {
//        this.userService = userService;
//    }
//
//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        logger.info("CustomUserDetailsService loading user by username/email: " + username);
//
//        User user = userService.findByEmail(username);
//        if (user == null) {
//            logger.warning("User not found with username/email: " + username);
//            throw new UsernameNotFoundException("User not found with username/email: " + username);
//        }
//
//        logger.info("User found: " + user.getUsername() + " with email: " + user.getEmail());
//
//        // Create and return Spring Security UserDetails object - matching UserService implementation
//        return new org.springframework.security.core.userdetails.User(
//                user.getEmail(), // Use email as the username for authentication
//                user.getPassword(),
//                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole()))
//        );
//    }
//}