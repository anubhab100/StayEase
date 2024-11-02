package com.crio.backend.service;

import com.crio.backend.controller.UserController;
import com.crio.backend.entity.User;
import com.crio.backend.repository.UserRepository;
import com.crio.backend.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class AuthenticationService {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    /**
     * Authenticate a user by verifying email and password.
     *
     * @param email    the email of the user
     * @param password the raw password of the user
     * @return the authenticated user
     * @throws Exception if the user is not found or the password is invalid
     */
    public User authenticate(String email, String password) throws Exception {
        // Retrieve user by email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        // Verify password
        logger.info(password);
        logger.info(user.getPassword());
        if (passwordEncoder.matches(password, user.getPassword())) {

            return user; // Authentication successful
        } else {
            logger.error("Invalid password for: {}", email);
            throw new Exception("Invalid password"); // Authentication failed
        }
    }

}
