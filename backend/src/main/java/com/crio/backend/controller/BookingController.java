package com.crio.backend.controller;

import com.crio.backend.dto.BookingDTO;
import com.crio.backend.entity.Booking;
import com.crio.backend.entity.User;
import com.crio.backend.security.JwtTokenUtil;
import com.crio.backend.service.BookingService;
import com.crio.backend.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private static final Logger logger = LoggerFactory.getLogger(BookingController.class);

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    // Endpoint for customers to book a room in a hotel
    @PostMapping("/hotels/{hotelId}/book")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Booking> bookRoom(
            @PathVariable Long hotelId,
            @RequestBody BookingDTO bookingDTO, // Accept BookingDTO from request body
            @RequestHeader("Authorization") String token) {

        logger.info("Attempting to book a room for hotel ID: {}", hotelId);

        // Extract the email of the authenticated user from the JWT token
        String jwtToken = token.substring(7); // Remove "Bearer " from the token
        String userEmail = jwtTokenUtil.getUsernameFromToken(jwtToken);
        logger.info("Booking request initiated by user: {}", userEmail);

        try {
            // Retrieve the authenticated user from the database
            User customer = userService.findByEmail(userEmail);

            // Set the user and hotelId in the BookingDTO
            bookingDTO.setUserId(customer.getId()); // Setting user ID in DTO
            bookingDTO.setHotelId(hotelId); // Setting hotel ID in DTO

            // Pass the BookingDTO to the service method
            Booking booking = bookingService.bookRoom(bookingDTO);
            logger.info("Room booked successfully for hotel ID: {} by user: {}", hotelId, customer.getEmail());
            return new ResponseEntity<>(booking, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Error occurred while booking room for hotel ID: {}", hotelId, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoint for hotel managers to cancel a booking
    @DeleteMapping("/{bookingId}")
    @PreAuthorize("hasRole('HOTEL_MANAGER')")
    public ResponseEntity<Void> cancelBooking(@PathVariable Long bookingId) {
        logger.info("Attempting to cancel booking with ID: {}", bookingId);

        try {
            bookingService.cancelBooking(bookingId);
            logger.info("Booking with ID: {} successfully canceled", bookingId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            logger.error("Error occurred while canceling booking with ID: {}", bookingId, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoint for viewing all bookings (Only for Admins)
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Booking>> getAllBookings() {
        logger.info("Admin is attempting to retrieve all bookings");

        try {
            List<Booking> bookings = bookingService.getAllBookings();
            logger.info("Successfully retrieved {} bookings", bookings.size());
            return new ResponseEntity<>(bookings, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred while retrieving all bookings", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
