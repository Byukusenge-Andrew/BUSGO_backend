package com.multi.mis.busgo_backend.service;

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
public class UserService implements UserDetailsService {
  private static  final Logger logger = Logger.getLogger(UserService.class.getName());
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


    public User findByUsername(String username) {
        logger.info("Finding user by username: " + username);
        return userRepository.findByUsername(username);
    }

    public User findByEmail(String email) {
        logger.info("Finding user by email: " + email);
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            logger.info("User found by email: " + email);
            return userOpt.get();
        } else {
            logger.info("User not found by email: " + email);
            return null;
        }
    }

    public User createUser(User user) {
        logger.info("Creating new user: " + user.getUsername() + ", " + user.getEmail());

        // Don't encode the password if it's already encoded
        if (user.getPassword() != null && !user.getPassword().startsWith("$2a$")) {
            logger.info("Encoding password for new user");
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        } else {
            logger.info("Password already encoded, skipping encoding");
        }

        User savedUser = userRepository.save(user);
        logger.info("User created successfully: " + savedUser.getUsername());
        return savedUser;
    }

    public Optional<User> getUserById(Long id) {
        logger.info("Getting user by ID: " + id);
        return userRepository.findById(id);
    }

    public List<User> getAllUsers() {
        logger.info("Getting all users");
        return userRepository.findAll();
    }

    public User updateUser(Long id, User user) {
        logger.info("Updating user with ID: " + id);
        User existingUser = userRepository.findById(id).orElse(null);
        if (existingUser != null) {
            if (user.getUsername() != null) existingUser.setUsername(user.getUsername());

            // Only encode the password if it's not already encoded
            if (user.getPassword() != null) {
                if (!user.getPassword().startsWith("$2a$")) {
                    logger.info("Encoding password for user update");
                    existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
                } else {
                    logger.info("Password already encoded, using as-is");
                    existingUser.setPassword(user.getPassword());
                }
            }

            if (user.getEmail() != null) existingUser.setEmail(user.getEmail());
            if (user.getRole() != null) existingUser.setRole(user.getRole());
            if (user.getFirstName() != null) existingUser.setFirstName(user.getFirstName());
            if (user.getLastName() != null) existingUser.setLastName(user.getLastName());
            if (user.getPhoneNumber() != null) existingUser.setPhoneNumber(user.getPhoneNumber());
            if (user.isActive() != existingUser.isActive()) existingUser.setActive(user.isActive());

            User updatedUser = userRepository.save(existingUser);
            logger.info("User updated successfully: " + updatedUser.getUsername());
            return updatedUser;
        }
        logger.warning("User not found for update with ID: " + id);
        return null;
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
//    public Optional<User> login(String username, String password) {
//        return userRepository.findByUsernameAndPassword(username, password);
//    }
}