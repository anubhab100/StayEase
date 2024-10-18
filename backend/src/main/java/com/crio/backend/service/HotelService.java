package com.crio.backend.service;

import com.crio.backend.dto.HotelDTO;
import com.crio.backend.entity.Hotel;
import com.crio.backend.exception.ResourceNotFoundException;
import com.crio.backend.repository.HotelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HotelService {

    @Autowired
    private HotelRepository hotelRepository;

    public Hotel createHotel(HotelDTO hotelDTO) {
        Hotel hotel = new Hotel();
        hotel.setName(hotelDTO.getName());
        hotel.setLocation(hotelDTO.getLocation());
        hotel.setDescription(hotelDTO.getDescription());
        hotel.setAvailableRooms(hotelDTO.getAvailableRooms());
        return hotelRepository.save(hotel);
    }

    public List<Hotel> getAllHotels() {
        return hotelRepository.findAll();
    }

    public Hotel updateHotel(Long hotelId, HotelDTO hotelDTO) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with id: " + hotelId));

        hotel.setName(hotelDTO.getName());
        hotel.setLocation(hotelDTO.getLocation());
        hotel.setDescription(hotelDTO.getDescription());
        hotel.setAvailableRooms(hotelDTO.getAvailableRooms());

        return hotelRepository.save(hotel);
    }

    public void deleteHotel(Long hotelId) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with id: " + hotelId));
        hotelRepository.delete(hotel);
    }

    public Hotel addHotel(Hotel hotel) {
        // Save the hotel entity to the database
        return hotelRepository.save(hotel);
    }

    public Hotel updateHotel(Long hotelId, Hotel hotelDetails) {
        // Find the hotel by its ID
        Hotel existingHotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with id: " + hotelId));

        // Update hotel details
        existingHotel.setName(hotelDetails.getName());
        existingHotel.setLocation(hotelDetails.getLocation());
        existingHotel.setDescription(hotelDetails.getDescription());
        existingHotel.setAvailableRooms(hotelDetails.getAvailableRooms());

        // Save the updated hotel to the database
        return hotelRepository.save(existingHotel);
    }

}
