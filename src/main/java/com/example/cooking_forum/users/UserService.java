package com.example.cooking_forum.users;

import com.example.cooking_forum.dishes.Dish;
import com.example.cooking_forum.dishes.DishRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;

import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final DishRepository dishRepository;
    private final PasswordEncoder encoder;
    private final MongoTemplate mongoTemplate;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username).orElseThrow(
                () -> new IllegalArgumentException("user not found")
        );
    }

    public User findUserById(String id){
       return userRepository.findById(id).orElseThrow(
               () -> new IllegalArgumentException("user not found"));
    }

    public Map<String,Boolean> userExistsById(List<String> userIds){
        Query query = new Query(Criteria.where("_id").in(userIds));
        List<User> users = mongoTemplate.find(query, User.class);

        Map<String, Boolean> resultMap = new HashMap<>();
        for(String userId : userIds){
            resultMap.put(userId,false);
        }

        for(User user : users){
            resultMap.put(user.getId(),true);
        }
        return resultMap;
    }

    public User findUserByEmail(String email){
        if(!isAdmin()){
            throw new AccessDeniedException("only admin can search users by email.");
        }

        return userRepository.findByEmail(email).orElseThrow(
                () -> new IllegalArgumentException("user not found"));
    }

    public void changePassword(String userId, PasswordChangeRequest request){
        User user = userRepository.findById(userId).orElseThrow(
                () -> new IllegalArgumentException("user not found"));

        if(!encoder.matches(request.oldPassword(), user.getPassword())){
            throw new IllegalArgumentException("Old password is incorrect!");
        }

        user.setPassword(encoder.encode(request.newPassword()));
        userRepository.save(user);
    }

    public User alterUser(String id, User alteredUser){
        User currentUser = userRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("user not found"));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if(isAdmin() || auth.getName().equals(currentUser.getUsername())){
            currentUser.setUsername(alteredUser.getUsername());
            currentUser.setEmail(alteredUser.getEmail());

            return userRepository.save(currentUser);
        }else{
            throw new AccessDeniedException("you can't alter other user's information.");
        }
    }

    public List<Dish> getFavouriteDishes(String userId){
        User user = userRepository.findById(userId).orElseThrow(
                () -> new IllegalArgumentException("user not found"));

        List<Dish> favouriteDishes = dishRepository.findByIdIn(user.getFavouriteDishIds());

        return favouriteDishes;
    }

    public boolean isFavouriteDish(String userId,String dishId){
        return userRepository.existsByIdAndFavouriteDishIdsContaining(userId,dishId);
    }

    public void addFavouriteDish(String userId, String dishId){
        User user = userRepository.findById(userId).orElseThrow(
                ()-> new IllegalArgumentException("user not found"));

        user.getFavouriteDishIds().add(dishId);
        userRepository.save(user);
    }

    public void deleteFavouriteDish(String userId, String dishId){
        User user = userRepository.findById(userId).orElseThrow(
                ()-> new IllegalArgumentException("user not found"));

        user.getFavouriteDishIds().remove(dishId);
        userRepository.save(user);
    }

    public void deleteById(String id){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("user not found"));

        if(isAdmin() || auth.getName().equals(user.getUsername())){
            userRepository.delete(user);
        }else{
            throw new AccessDeniedException("you can't delete other user.");
        }
    }

    private static boolean isAdmin(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getAuthorities().stream().anyMatch(
                authority ->
                        authority.getAuthority().equals("ADMIN"));
    }
}
