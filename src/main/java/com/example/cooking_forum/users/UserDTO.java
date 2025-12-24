package com.example.cooking_forum.users;

import com.example.cooking_forum.dishes.Dish;
import lombok.Builder;

import java.util.List;

@Builder
public record UserDTO(String id, String username, AppUserRole role,
                      List<Dish> publishedDishes) {
}
