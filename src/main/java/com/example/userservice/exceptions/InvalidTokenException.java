package com.example.userservice.exceptions;

public class InvalidTokenException extends Throwable {
    public InvalidTokenException(String invalidToken) {
        super(invalidToken);
    }
}
