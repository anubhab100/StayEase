package com.crio.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingDTO {

    private Long hotelId;
    private Long userId; // Optional if you are taking user from session/JWT
    private String bookingDate; // Could be LocalDate if needed
}
