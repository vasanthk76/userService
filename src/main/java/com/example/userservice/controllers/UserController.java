package com.example.userservice.controllers;

import com.example.userservice.dtos.LoginRequestDto;
import com.example.userservice.dtos.LogoutRequestDto;
import com.example.userservice.dtos.UserDto;
import com.example.userservice.dtos.SignupRequestDto;
import com.example.userservice.exceptions.InvalidPasswordException;
import com.example.userservice.exceptions.InvalidTokenException;
import com.example.userservice.models.Token;
import com.example.userservice.models.User;
import com.example.userservice.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {
    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    public UserDto signup(@RequestBody SignupRequestDto requestDto){

        User user = userService.signup(requestDto.getUsername(),requestDto.getEmail(),requestDto.getPassword(),requestDto.getRoles());

        return UserDto.from(user);
    }

    @PostMapping("/login")
    public Token login(@RequestBody LoginRequestDto requestDto) throws InvalidPasswordException {
        return userService.login(requestDto.getEmail(),requestDto.getPassword());
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestBody LogoutRequestDto requestDto){
        try{
            userService.logout(requestDto.getToken());
            return new ResponseEntity<String>("logout success",HttpStatus.OK);
        }catch(InvalidTokenException e){
            return new ResponseEntity<String>("bad request",HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/validate/{token}")
    public UserDto validateToken(@PathVariable String token) {
        try{
            return UserDto.from(userService.validateToken(token));
        }catch(InvalidTokenException e){
            return null;
        }
    }

    @GetMapping("/{id}")
    public String getUsers(@PathVariable("id") Long id){
        System.out.println("received request for a user");
        return "hello from user service " + id;
    }
}
