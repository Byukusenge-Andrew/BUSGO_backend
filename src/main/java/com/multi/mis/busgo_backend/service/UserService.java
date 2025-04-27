package com.multi.mis.busgo_backend.service;

import org.springframework.context.annotation.Primary;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.multi.mis.busgo_backend.model.User;
import com.multi.mis.busgo_backend.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Service
@Primary
public class UserService implements UserDetailsService {
    private static final Logger logger = Logger.getLogger(UserService.class.getName());
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        logger.info("Loading user by username/email: " + usernameOrEmail);

        // First try to find by email
        Optional<User> userByEmail = userRepository.findByEmail(usernameOrEmail);

        // If not found by email, try by username
        User user = userByEmail.orElseGet(() -> userRepository.findByUsername(usernameOrEmail));

        if (user == null) {
            logger.warning("User not found with username/email: " + usernameOrEmail);
            throw new UsernameNotFoundException("User not found with username/email: " + usernameOrEmail);
        }

        logger.info("User found: " + user.getUsername() + " with email: " + user.getEmail());

        // Create and return Spring Security UserDetails object
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(), // Use email as the username for authentication
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole()))
        );
    }

    // Other methods remain unchanged
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public User createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User updateUser(Long id, User user) {
        return userRepository.findById(id)
                .map(existingUser -> {
                    if (user.getUsername() != null) {
                        existingUser.setUsername(user.getUsername());
                    }
                    if (user.getEmail() != null) {
                        existingUser.setEmail(user.getEmail());
                    }
                    if (user.getPassword() != null) {
                        existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
                    }
                    if (user.getFirstName() != null) {
                        existingUser.setFirstName(user.getFirstName());
                    }
                    if (user.getLastName() != null) {
                        existingUser.setLastName(user.getLastName());
                    }
                    if (user.getPhoneNumber() != null) {
                        existingUser.setPhoneNumber(user.getPhoneNumber());
                    }
                    if (user.getRole() != null) {
                        existingUser.setRole(user.getRole());
                    }
                    existingUser.setActive(user.isActive());
                    return userRepository.save(existingUser);
                })
                .orElse(null);
    }

    public boolean deleteUser(Long id) {
        logger.info("Deleting user with ID: " + id);
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            logger.info("User deleted successfully with ID: " + id);
            return true;
        }
        logger.warning("User not found for deletion with ID: " + id);
        return false;
    }

    public List<User> getUsersByRole(String role) {
        logger.info("Getting users by role: " + role);
        return userRepository.findByRole(role);
    }

    public List<User> getActiveUsers() {
        logger.info("Getting active users");
        return userRepository.findByIsActiveTrue();
    }
}