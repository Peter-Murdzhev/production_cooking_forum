package com.example.cooking_forum.auth;

import com.example.cooking_forum.users.UserDTO;
import com.mongodb.MongoWriteException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthenticationController {
    private final AuthenticationService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid RegisterRequest request,
                                      BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            Map<String,String> errors = new HashMap<>();

            for(FieldError error : bindingResult.getFieldErrors()){
                errors.put(error.getField(),error.getDefaultMessage());
            }

            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }

        try {
            return ResponseEntity.ok(authService.register(request));
        }catch (MongoWriteException mwe){
            return new ResponseEntity<>("user already exists with this email or username",
                    HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthenticationRequest request){
        try {
            return ResponseEntity.ok(authService.login(request));
        }catch (AuthenticationException ae){
            Map<String,String> error = new HashMap<>();
            error.put("authentication","Username or password incorrect!");

            return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/check-auth")
    public ResponseEntity<UserDTO> checkAuth(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        try {
           return ResponseEntity.ok(authService.checkAuth(authentication));
        }catch (IllegalStateException ise){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        try{
            request.logout();
            return ResponseEntity.ok("logged out");
        }catch (ServletException sle){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("logout failed");
        }
    }
}
