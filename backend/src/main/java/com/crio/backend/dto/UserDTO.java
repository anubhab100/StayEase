package com.crio.backend.dto;

import com.crio.backend.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private Role role; // Optional: Defaults to CUSTOMER if not provided
}
