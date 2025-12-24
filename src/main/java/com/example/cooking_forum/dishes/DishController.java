package com.example.cooking_forum.dishes;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/api/v1/dish")
@RequiredArgsConstructor
public class DishController {
    private final DishService dishService;

    @GetMapping("/find/id/{id}")
    public ResponseEntity<?> findDishById(@PathVariable String id){
        try {
            return ResponseEntity.ok(dishService.findById(id));
        }catch (IllegalArgumentException iae){
            return new ResponseEntity<>("dish isn't found.", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/find/name/{name}")
    public ResponseEntity<List<Dish>> findDishesByName(@PathVariable String name){
        return ResponseEntity.ok(dishService.findByName(name));
    }

    @GetMapping("/find/userid/{id}")
    public ResponseEntity<List<Dish>> findDishesByUser(@PathVariable String id){
        return ResponseEntity.ok(dishService.findDishesByUser(id));
    }

    @GetMapping("/find/all")
    public ResponseEntity<List<Dish>> findAllDishes(){
        return ResponseEntity.ok(dishService.findAllDishes());
    }

    @PostMapping("/add")
    public ResponseEntity<?> addDish(@RequestBody @Valid Dish dish,
                                        BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            HashMap<String,String> errors = new HashMap<>();

            for(FieldError error : bindingResult.getFieldErrors()){
                errors.put(error.getField(), error.getDefaultMessage());
            }

            return new ResponseEntity<>(errors,HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok(dishService.addDish(dish));
    }

    @PutMapping("/add/comment/{dishId}")
    public ResponseEntity<?> addComment(@PathVariable String dishId,
                                        @RequestBody @NotNull String message){
        try {
            return ResponseEntity.ok(dishService.addComment(dishId, message));
        }catch (IllegalArgumentException iae){
            return new ResponseEntity<>("dish  isn't found.", HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("{dishId}/remove/comment/{commentIndex}")
    public ResponseEntity<?> removeComment(@PathVariable String dishId,
                                 @PathVariable int commentIndex){
        try{
            return ResponseEntity.ok(dishService.deleteComment(dishId, commentIndex));
        }catch (IllegalArgumentException iae){
            return new ResponseEntity<>("deleting comment failed", HttpStatus.BAD_REQUEST);
        }catch (IllegalStateException ise){
            return new ResponseEntity<>("you can't alter other user's dishes!",
                    HttpStatus.FORBIDDEN);
        }
    }

    @PutMapping("/alter/{id}")
    public ResponseEntity<?> alterDish(@PathVariable String id,
                          @RequestBody @Valid Dish dish,BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            HashMap<String,String> errors = new HashMap<>();

            for(FieldError error : bindingResult.getFieldErrors()){
                errors.put(error.getField(), error.getDefaultMessage());
            }

            return new ResponseEntity<>(errors,HttpStatus.BAD_REQUEST);
        }

        try {
            return ResponseEntity.ok(dishService.alterDish(id,dish));
        }catch (IllegalArgumentException iae){
            return new ResponseEntity<>("dish isn't found", HttpStatus.BAD_REQUEST);
        }catch (UsernameNotFoundException unnfe){
            return new ResponseEntity<>("user not found", HttpStatus.BAD_REQUEST);
        }catch (AccessDeniedException ade){
            return new ResponseEntity<>("you can't alter the dish of another user.",
                    HttpStatus.FORBIDDEN);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteDish(@PathVariable String id) throws IOException {
        return ResponseEntity.ok(dishService.deleteDish(id));
    }
}
