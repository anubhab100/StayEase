package com.crio.backend.service;

import com.crio.backend.dto.BookingDTO;
import com.crio.backend.entity.Booking;
import com.crio.backend.entity.BookingStatus;
import com.crio.backend.entity.Hotel;
import com.crio.backend.entity.User;
import com.crio.backend.exception.BadRequestException;
import com.crio.backend.exception.ResourceNotFoundException;
import com.crio.backend.repository.BookingRepository;
import com.crio.backend.repository.HotelRepository;
import com.crio.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private UserRepository userRepository;

    public Booking bookRoom(BookingDTO bookingDTO) {
        // Check if the hotel exists
        Hotel hotel = hotelRepository.findById(bookingDTO.getHotelId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Hotel not found with id: " + bookingDTO.getHotelId()));

        // Check if the hotel has available rooms
        if (hotel.getAvailableRooms() <= 0) {
            throw new BadRequestException("No available rooms in the hotel.");
        }

        // Find the user (User ID is set in the DTO from the controller)
        User user = userRepository.findById(bookingDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + bookingDTO.getUserId()));

        // Create a new booking entity using data from the DTO
        Booking booking = new Booking();
        booking.setHotel(hotel);
        booking.setUser(user);
        booking.setBookingDate(LocalDate.parse(bookingDTO.getBookingDate())); // Assuming bookingDTO has a date
        booking.setStatus(BookingStatus.ACTIVE);

        // Decrease available rooms count in the hotel
        hotel.setAvailableRooms(hotel.getAvailableRooms() - 1);
        hotelRepository.save(hotel); // Save the updated hotel

        // Save and return the booking entity
        return bookingRepository.save(booking);
    }

    public void cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));

        // Only cancel active bookings
        if (booking.getStatus() != BookingStatus.ACTIVE) {
            throw new BadRequestException("Booking is not active and cannot be canceled.");
        }

        // Set booking status to cancelled
        booking.setStatus(BookingStatus.CANCELLED);

        // Increase available rooms count
        Hotel hotel = booking.getHotel();
        hotel.setAvailableRooms(hotel.getAvailableRooms() + 1);
        hotelRepository.save(hotel);

        bookingRepository.save(booking);
    }

    public List<Booking> getBookingsByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        return bookingRepository.findByUser(user);
    }

    public List<Booking> getAllBookings() {
        // Fetch all bookings from the repository
        return bookingRepository.findAll();
    }
}
