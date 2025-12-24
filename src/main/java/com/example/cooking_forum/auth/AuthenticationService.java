package com.example.cooking_forum.auth;

import com.example.cooking_forum.dishes.Dish;
import com.example.cooking_forum.dishes.DishService;
import com.example.cooking_forum.users.AppUserRole;
import com.example.cooking_forum.users.User;
import com.example.cooking_forum.users.UserDTO;
import com.example.cooking_forum.users.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authManager;
    private final DishService dishService;

    public String register(RegisterRequest request) {
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(AppUserRole.USER)
                .favouriteDishIds(new ArrayList<>())
                .build();

        userRepository.save(user);
        return "Successfully registered";
    }

    public UserDTO login(AuthenticationRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("user not found"));

        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(), request.getPassword())
        );

        List<Dish> dishesByUser = dishService.findDishesByUser(user.getId());

        return new UserDTO(user.getId(),
                user.getUsername(), user.getRole(), dishesByUser);
    }

    public UserDTO checkAuth(Authentication authentication){
        if(authentication == null){
            return null;
        }

        User user = (User) authentication.getPrincipal();
        List<Dish> dishesByUser = dishService.findDishesByUser(user.getId());

        return new UserDTO(user.getId(), user.getUsername(), user.getRole(), dishesByUser);
    }
}
