package com.example.cooking_forum.users;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Document
public class User implements UserDetails {
    @Id
    private String id;
    @Indexed(unique = true)
    @Size(min = 6, max = 50)
    private String username;
    @Indexed(unique = true)
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$",
            message = "Please enter a valid Email!")
    private String email;
    @Size(min = 6, max = 30)
    private String password;
    private AppUserRole role;

    private List<String> favouriteDishIds;

    public User(String username, String email, String password,
                AppUserRole role, List<String> favouriteDishIds) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
        this.favouriteDishIds = favouriteDishIds;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword(){
        return password;
    }

    public void setUsername(String username) {
        if(username != null){
            this.username = username;
        }
    }

    public void setEmail(String email) {
        if(email != null){
            this.email = email;
        }
    }

    public void setPassword(String password) {
        if(password != null){
            this.password = password;
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}
