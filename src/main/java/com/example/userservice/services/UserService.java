package com.example.userservice.services;

import com.example.userservice.confirgurations.KafkaProducerClient;
import com.example.userservice.dtos.SendEmailDto;
import com.example.userservice.exceptions.InvalidPasswordException;
import com.example.userservice.exceptions.InvalidTokenException;
import com.example.userservice.models.Role;
import com.example.userservice.models.Token;
import com.example.userservice.models.User;
import com.example.userservice.repositories.RoleRepository;
import com.example.userservice.repositories.TokenRepository;
import com.example.userservice.repositories.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private UserRepository userRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private TokenRepository tokenRepository;
    private RoleRepository roleRepository;
    private KafkaProducerClient kafkaProducer;
    private ObjectMapper objectMapper;

    public UserService(UserRepository userRepository,BCryptPasswordEncoder bCryptPasswordEncoder,TokenRepository tokenRepository,RoleRepository roleRepository,KafkaProducerClient kafkaProducer, ObjectMapper objectMapper) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.tokenRepository = tokenRepository;
        this.roleRepository = roleRepository;
        this.kafkaProducer = kafkaProducer;
        this.objectMapper = objectMapper;
    }

    public User signup(String name, String email, String password, List<String> roles)  {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if(optionalUser.isPresent()) {
            return optionalUser.get();
        }

        List<Role> userRoles = roles.stream().map(role-> {
            Role userRole;
            if(roleRepository.findByRoleName(role).isPresent()) {
                userRole = roleRepository.findByRoleName(role).get();
            } else {
                userRole = new Role();
                userRole.setRoleName(role);
                roleRepository.save(userRole);
            }
            return userRole;
        }).toList();

        User user = new User();
        user.setEmail(email);
        user.setName(name);
        user.setIsEmailVerified(false);
        user.setRoles(userRoles);

        user.setHashedPassword(bCryptPasswordEncoder.encode(password));

        //once the sign is complete, send a message to kafka for sending an email to the user.
        SendEmailDto emailDto = new SendEmailDto();
        emailDto.setTo(user.getEmail());
        emailDto.setSubject("welcome to the site");
        emailDto.setFrom("admin@ecommerce.com");
        emailDto.setBody("thanks for joining us");

//        try{
//            kafkaProducer.sendMessage("userSignUp",objectMapper.writeValueAsString(user));
//        }
//        catch(JsonProcessingException e){
//            System.out.println("something went wrong while sending a message to kafka "+e);
//        }
        return userRepository.save(user);
    }

    public Token login(String email, String password) throws InvalidPasswordException {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if(optionalUser.isEmpty()) {
            return null;
        }
        User user=optionalUser.get();
        if(!bCryptPasswordEncoder.matches(password,user.getHashedPassword())){
            throw new InvalidPasswordException("Invalid password!");
//            return null;
        }
        Token token = generateToken(user);
        return tokenRepository.save(token);
    }

    private Token generateToken(User user) {
        Token token = new Token();

        LocalDate localDate = LocalDate.now().plusDays(30);
        Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

        token.setDeleted(false);
        token.setExpires(date);

        token.setValue(RandomStringUtils.randomAlphanumeric(128));
        token.setUser(user);
        return token;
    }

    public void logout(String tokenValue) throws InvalidTokenException {
        Optional<Token> optionalToken = tokenRepository.findByValueAndDeleted(tokenValue,false);
        if(optionalToken.isEmpty()){
            throw new InvalidTokenException("invalid token");
        }
        Token token = optionalToken.get();
        token.setDeleted(true);
        tokenRepository.save(token);
    }

    public User validateToken(String token) throws InvalidTokenException {
        Optional<Token> optionalToken = tokenRepository.findByValueAndDeleted(token,false);
        if(optionalToken.isEmpty()){
            throw new InvalidTokenException("invalid token");
        }

        return optionalToken.get().getUser();
    }
}
