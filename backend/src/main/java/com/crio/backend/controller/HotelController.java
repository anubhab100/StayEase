package com.crio.backend.controller;

import com.crio.backend.entity.Hotel;
import com.crio.backend.service.HotelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hotels")
public class HotelController {

    private static final Logger logger = LoggerFactory.getLogger(HotelController.class);

    @Autowired
    private HotelService hotelService;

    // Endpoint for browsing all available hotels (Public endpoint)
    @GetMapping
    public ResponseEntity<List<Hotel>> getAllHotels() {
        logger.info("Retrieving all available hotels");
        try {
            List<Hotel> hotels = hotelService.getAllHotels();
            logger.info("Successfully retrieved {} hotels", hotels.size());
            return new ResponseEntity<>(hotels, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred while retrieving hotels", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoint for adding a new hotel (Only ADMIN can create a hotel)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Hotel> addHotel(@RequestBody Hotel hotel) {
        logger.info("Attempting to add a new hotel: {}", hotel.getName());
        try {
            Hotel createdHotel = hotelService.addHotel(hotel);
            logger.info("Successfully added hotel: {}", hotel.getName());
            return new ResponseEntity<>(createdHotel, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Error occurred while adding hotel: {}", hotel.getName(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoint for updating hotel details (Only HOTEL_MANAGER can update hotel
    // details)
    @PutMapping("/{hotelId}")
    @PreAuthorize("hasRole('HOTEL_MANAGER')")
    public ResponseEntity<Hotel> updateHotel(@PathVariable Long hotelId, @RequestBody Hotel hotelDetails) {
        logger.info("Attempting to update hotel with ID: {}", hotelId);
        try {
            Hotel updatedHotel = hotelService.updateHotel(hotelId, hotelDetails);
            logger.info("Successfully updated hotel with ID: {}", hotelId);
            return new ResponseEntity<>(updatedHotel, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred while updating hotel with ID: {}", hotelId, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoint for deleting a hotel (Only ADMIN can delete a hotel)
    @DeleteMapping("/{hotelId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteHotel(@PathVariable Long hotelId) {
        logger.info("Attempting to delete hotel with ID: {}", hotelId);
        try {
            hotelService.deleteHotel(hotelId);
            logger.info("Successfully deleted hotel with ID: {}", hotelId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            logger.error("Error occurred while deleting hotel with ID: {}", hotelId, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
