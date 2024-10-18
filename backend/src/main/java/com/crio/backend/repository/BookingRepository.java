package com.crio.backend.repository;

import com.crio.backend.entity.Booking;
import com.crio.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    // Find all bookings for a specific user
    List<Booking> findByUser(User user);

    // Optional: Add custom query methods if needed, e.g., find by status
}
