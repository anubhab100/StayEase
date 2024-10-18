package com.crio.backend.service;

import com.crio.backend.controller.UserController;
import com.crio.backend.entity.User;
import com.crio.backend.repository.UserRepository;
import com.crio.backend.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class AuthenticationService {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

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
        if (passwordEncoder.matches(password, user.getPassword())) {

            return user; // Authentication successful
        } else {
            throw new Exception("Invalid password"); // Authentication failed
        }
    }

    public void testPasswordEncoding() {
        String rawPassword = "1234"; // The password you intend to use
        String encodedPassword = passwordEncoder.encode(rawPassword);

        // Log the encoded password
        System.out.println("Encoded password: " + encodedPassword);

        // Check if it matches
        boolean matches = passwordEncoder.matches(rawPassword, encodedPassword);
        System.out.println("Password match test: " + matches); // Should print true
    }
}
