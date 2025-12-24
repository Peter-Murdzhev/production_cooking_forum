package com.example.cooking_forum.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class AuthenticationRequest {
    private String username;
    private String password;
}
