package com.example.cooking_forum.auth;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RegisterRequest{
    @Size(min = 6,message = "Username must be 6 characters or longer!")
    private String username;
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$",
            message = "Please enter a valid email!")
    private String email;
    @Size(min = 6, message = "Password must be 6 characters or longer!")
    private String password;
}
