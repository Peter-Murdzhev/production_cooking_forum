package com.example.cooking_forum.dishes;

import com.example.cooking_forum.file.FileService;
import com.example.cooking_forum.users.User;
import com.example.cooking_forum.users.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DishService {
    private final DishRepository dishRepository;
    private final UserRepository userRepository;
    private final FileService fileService;

    public Dish findById(String id){
        return dishRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("dish isn't found."));
    }

    public List<Dish> findByName(String name){
        return dishRepository.findByName(name);
    }

    public List<Dish> findDishesByUser(String id){
        return dishRepository.findByOwnerOfDishId(id);
    }

    public List<Dish> findAllDishes(){
        return dishRepository.findAll();
    }

    public Dish addDish(Dish dish){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(auth.getName()).orElseThrow(
                () -> new UsernameNotFoundException("user not found"));

        dish.setOwnerOfDishId(user.getId());
        dish.setPublishedDateTime(LocalDateTime.now());
        dish.setDishComments(new ArrayList<>());

        return dishRepository.save(dish);
    }

    public Dish addComment(String dishId, String message){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        User user = userRepository.findByUsername(auth.getName()).orElseThrow(
                () -> new UsernameNotFoundException("username not found."));

        Dish dish = dishRepository.findById(dishId).orElseThrow(
                () -> new IllegalArgumentException("dish isn't found."));

        Comment comment = Comment.builder()
                        .userId(user.getId())
                        .username(user.getUsername())
                        .message(message)
                        .postDateTime(LocalDateTime.now())
                        .build();

        dish.getDishComments().add(comment);
        return dishRepository.save(dish);
    }

    public Dish deleteComment(String dishId, int commentIndex){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        Dish dish = dishRepository.findById(dishId).orElseThrow(
                () -> new IllegalArgumentException("dish isn't found"));
        Comment comment = dish.getDishComments().get(commentIndex);
        boolean isAdmin = auth.getAuthorities().stream().anyMatch(
                authority ->
                        authority.getAuthority().equals("ADMIN"));

        if(isAdmin || auth.getName().equals(comment.getUsername())){
            dish.getDishComments().remove(commentIndex);
            return dishRepository.save(dish);
        }else{
            throw new IllegalStateException("you can't remove other user's comments!");
        }
    }

    public Dish alterDish(String id, Dish alteredDish){
        Dish currentDish = dishRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("dish isn't found."));

        User user = userRepository.findById(currentDish.getOwnerOfDishId()).orElse(null);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream().anyMatch(
                authority ->
                        authority.getAuthority().equals("ADMIN"));

        if(isAdmin || (user != null && auth.getName().equals(user.getUsername()))){
            currentDish.setName(alteredDish.getName());
            currentDish.setDescription(alteredDish.getDescription());
            currentDish.setIngredients(alteredDish.getIngredients());
            currentDish.setHowToPrepare(alteredDish.getHowToPrepare());
            currentDish.setImageSource(alteredDish.getImageSource());
            currentDish.setImagePublicId(alteredDish.getImagePublicId());

            return dishRepository.save(currentDish);
        }else{
            throw new AccessDeniedException("you can't alter other user's dish");
        }
    }

    public String deleteDish(String id) throws IOException {
        Dish dish = dishRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("dish isn't found"));

        User user = userRepository.findById(dish.getOwnerOfDishId())
                .orElse(null);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        boolean isAdmin = auth.getAuthorities().stream().anyMatch(
                authority ->
                        authority.getAuthority().equals("ADMIN"));

        if (isAdmin || (user != null && auth.getName().equals(user.getUsername()))) {
            String imageId = dish.getImagePublicId();
            fileService.deleteImage(imageId);

            dishRepository.deleteById(id);

            return "dish successfully deleted.";
        }else{
            throw new AccessDeniedException("you can't delete this dish");
        }
    }
}
