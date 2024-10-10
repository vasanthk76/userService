package com.example.userservice.models;

import jakarta.persistence.Entity;
import lombok.Data;

@Data
@Entity
public class Role extends BaseClass{
    private String roleName;
}
