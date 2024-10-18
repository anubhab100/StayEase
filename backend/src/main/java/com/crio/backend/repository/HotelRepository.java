package com.crio.backend.repository;

import com.crio.backend.entity.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long> {

    // You can add custom query methods if needed, e.g., find by location, etc.
}
