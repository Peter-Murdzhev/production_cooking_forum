package com.example.cooking_forum.users;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean  existsByIdAndFavouriteDishIdsContaining(String userId, String dishId);
}
