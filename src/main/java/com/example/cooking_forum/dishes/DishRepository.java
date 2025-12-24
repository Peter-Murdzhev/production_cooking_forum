package com.example.cooking_forum.dishes;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DishRepository extends MongoRepository<Dish,String> {
    List<Dish> findByName(String name);
    List<Dish> findByOwnerOfDishId(String id);
    List<Dish> findByIdIn(List<String> favouritesId);
}
