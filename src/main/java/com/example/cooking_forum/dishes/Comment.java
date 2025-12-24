package com.example.cooking_forum.dishes;

import com.example.cooking_forum.users.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class Comment {
    @NotNull
    private String userId;
    @NotNull
    private String username;
    @NotNull
    private String message;
    @NotNull
    @JsonFormat(pattern = "dd.MM.yyyy HH:mm")
    private LocalDateTime postDateTime;
}

