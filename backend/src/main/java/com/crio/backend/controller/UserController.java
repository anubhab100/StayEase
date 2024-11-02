package com.crio.backend.controller;

import com.crio.backend.dto.UserDTO;
import com.crio.backend.entity.User;
import com.crio.backend.security.JwtTokenUtil;
import com.crio.backend.service.AuthenticationService;

import com.crio.backend.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // Endpoint for user registration (Public)
    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody UserDTO userDTO) {
        logger.info("Attempting to register a new user: {}", userDTO.getEmail());

        try {
            // Encrypt password before saving
            String encodedPassword = passwordEncoder.encode(userDTO.getPassword());
            userDTO.setPassword(encodedPassword);

            User createdUser = userService.registerUser(userDTO);
            System.out.println("The encoded password;" + encodedPassword);
            logger.info("User registered successfully: {}", createdUser.getEmail());

            // Generate JWT token for the new user
            String token = jwtTokenUtil.generateToken(createdUser.getEmail());
            logger.info("JWT token generated for user: {}", createdUser.getEmail());

            return ResponseEntity.status(HttpStatus.CREATED).header("Authorization", "Bearer " + token)
                    .body(createdUser);
        } catch (Exception e) {
            logger.error("Error occurred during user registration: {}", userDTO.getEmail(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoint for user login (Public)
    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody UserDTO userDTO) {
        logger.info("Login attempt for user: {}", userDTO.getEmail());

        try {
            // Authenticate user
            User authenticatedUser = authenticationService.authenticate(userDTO.getEmail(), userDTO.getPassword());
            logger.info("User authenticated successfully: {}", authenticatedUser.getEmail());

            // Generate JWT token
            String token = jwtTokenUtil.generateToken(authenticatedUser.getEmail());
            logger.info("JWT token generated for user: {}", authenticatedUser.getEmail());

            return ResponseEntity.ok().header("Authorization", "Bearer " + token).body("Login successful");
        } catch (Exception e) {
            logger.error("Login failed for user: {}", userDTO.getEmail(), e);
            return new ResponseEntity<>("Invalid credentials", HttpStatus.UNAUTHORIZED);
        }
    }

    // Endpoint to fetch user profile (Private - Authenticated users only)
    @GetMapping("/profile")
    public ResponseEntity<User> getUserProfile(@RequestHeader("Authorization") String token) {
        logger.info("Attempting to fetch user profile");

        try {
            String jwtToken = token.substring(7); // Remove "Bearer " from the token
            String userEmail = jwtTokenUtil.getUsernameFromToken(jwtToken);

            User user = userService.findByEmail(userEmail);
            logger.info("User profile retrieved for user: {}", userEmail);

            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred while retrieving user profile", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
