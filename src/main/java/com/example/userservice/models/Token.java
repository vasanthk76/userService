package com.example.userservice.models;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Data;

import java.util.Date;

@Entity
@Data
public class Token extends BaseClass{
    private String value;
    private Date expires;
    @ManyToOne
    private User user;
}
