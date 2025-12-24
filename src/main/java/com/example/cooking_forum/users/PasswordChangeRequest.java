package com.example.cooking_forum.users;

import jakarta.validation.constraints.Size;

public record PasswordChangeRequest(String oldPassword,
                                    @Size(min=6) String newPassword) {
}
