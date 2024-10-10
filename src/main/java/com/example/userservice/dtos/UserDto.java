package com.example.userservice.dtos;

import com.example.userservice.models.Role;
import com.example.userservice.models.User;
import jakarta.persistence.ManyToMany;
import lombok.Data;

import java.util.List;

@Data
public class UserDto {
    private String username;
    private String email;
    @ManyToMany
    private List<Role> roles;
    private Boolean isVerified;

    public static UserDto from(User user) {
        UserDto userDto = new UserDto();
        userDto.setEmail(user.getEmail());
        userDto.setRoles(user.getRoles());
        userDto.setUsername(user.getName());
        userDto.setIsVerified(user.getIsEmailVerified());
        return userDto;
    }
}
