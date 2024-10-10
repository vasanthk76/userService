package com.example.userservice.models;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import lombok.Data;

import java.util.List;

@Data
@Entity
public class User extends BaseClass{
    private String name;
    private String hashedPassword;
    private String email;
    @ManyToMany
    private List<Role> roles;
    private Boolean isEmailVerified;
}
