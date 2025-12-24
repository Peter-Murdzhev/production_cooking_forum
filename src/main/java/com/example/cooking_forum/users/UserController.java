package com.example.cooking_forum.users;

import com.example.cooking_forum.dishes.Dish;
import com.example.cooking_forum.dishes.DishService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final DishService dishService;

    @GetMapping("/findbyid/{id}")
    public ResponseEntity<?> findUserById(@PathVariable String id) {
        User user = userService.findUserById(id);
        List<Dish> dishesByUser = dishService.findDishesByUser(user.getId());

        UserDTO userDTO = new UserDTO(user.getId(), user.getUsername(),
                user.getRole(), dishesByUser);
        try {
            return ResponseEntity.ok(userDTO);
        } catch (IllegalArgumentException iae) {
            return new ResponseEntity<>("this user doesn't exist.", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/many/existsbyid")
    public Map<String,Boolean> existsById(@RequestBody List<String> userIds) {
        return userService.userExistsById(userIds);
    }

    @GetMapping("/findbyemail/{email}")
    public ResponseEntity<?> findUserByEmail(@PathVariable String email) {
        User user = userService.findUserByEmail(email);
        List<Dish> dishesByUser = dishService.findDishesByUser(user.getId());

        UserDTO userDTO = new UserDTO(user.getId(), user.getUsername(),
                user.getRole(), dishesByUser);
        try {
            return ResponseEntity.ok(userDTO);
        } catch (IllegalArgumentException iae) {
            return new ResponseEntity<>("User with this email can't be found.",
                    HttpStatus.BAD_REQUEST);
        } catch (AccessDeniedException ade) {
            return new ResponseEntity<>("only admin can search users by email.",
                    HttpStatus.FORBIDDEN);
        }
    }

    @PutMapping("/change-password/{userId}")
    public ResponseEntity<?> changePassword(@PathVariable String userId,
                                            @RequestBody @Valid PasswordChangeRequest request,
                                            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>("Password can't be less than 6 symbols",
                    HttpStatus.BAD_REQUEST);
        }

        try {
            userService.changePassword(userId, request);
            return ResponseEntity.ok("Password successfully changed!");
        } catch (IllegalArgumentException iae) {
            return new ResponseEntity<>(iae.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/alter/{id}")
    public ResponseEntity<?> alterUser(@PathVariable String id, @RequestBody @Valid User user) {
        try {
            return ResponseEntity.ok(userService.alterUser(id, user));
        } catch (IllegalArgumentException iae) {
            return new ResponseEntity<>("user isn't found", HttpStatus.BAD_REQUEST);
        } catch (AccessDeniedException ade) {
            return new ResponseEntity<>("you can't alter other user's information",
                    HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/{userId}/get/favouritedishes")
    public List<Dish> getFavouriteDishes(@PathVariable String userId) {
        return userService.getFavouriteDishes(userId);
    }

    @GetMapping("/{userId}/dish/isfavourite/{dishId}")
    public boolean isFavouriteDish(@PathVariable String userId,
                                   @PathVariable String dishId) {
        return userService.isFavouriteDish(userId, dishId);
    }

    @PostMapping("{userId}/add/favouritedish/{dishId}")
    public void addFavouriteDish(@PathVariable String userId,
                                 @PathVariable String dishId) {
        userService.addFavouriteDish(userId, dishId);
    }

    @DeleteMapping("{userId}/delete/favouriteDish/{dishId}")
    public void deleteFavouriteDish(@PathVariable String userId,
                                    @PathVariable String dishId) {
        userService.deleteFavouriteDish(userId, dishId);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable String id) {
        try {
            userService.deleteById(id);
            return ResponseEntity.ok("successfully deleted user.");
        } catch (IllegalArgumentException iae) {
            return new ResponseEntity<>("user isn't found",
                    HttpStatus.BAD_REQUEST);
        } catch (AccessDeniedException ade) {
            return new ResponseEntity<>("you can't delete other user.",
                    HttpStatus.FORBIDDEN);
        }
    }
}
