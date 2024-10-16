package com.example.userservice.dtos;

import lombok.Data;

import java.util.List;

@Data
public class SignupRequestDto {
    private String username;
    private String password;
    private String email;
    private List<String> roles;
}
