package com.example.kpo4.controller;
import com.example.kpo4.dto.UserLoginDTO;
import com.example.kpo4.dto.UserRegistrationDTO;
import com.example.kpo4.model.UserModel;
import com.example.kpo4.model.SessionModel;
import com.example.kpo4.service.UserService;
import com.example.kpo4.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
public class UserController {
    private final UserService userService;
    private final SessionService sessionService;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserController(UserService userService, SessionService sessionService, BCryptPasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.sessionService = sessionService;
        this.passwordEncoder = passwordEncoder;
    }
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserRegistrationDTO userDto) {
        if (userService.findByUsername(userDto.getUsername()) != null || userService.findByEmail(userDto.getEmail()) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username or email already exists.");
        }

        UserModel user = new UserModel();
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setRole(userDto.getRole());
        user.setPasswordHash(passwordEncoder.encode(userDto.getPassword()));
        userService.save(user);

        return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully.");
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody UserLoginDTO userDto) {
        UserModel user = userService.findByUsername(userDto.getUsername());
        if (user == null || !passwordEncoder.matches(userDto.getPassword(), user.getPasswordHash())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password.");
        }

        SessionModel session = new SessionModel();
        session.setUser(user.getId());
        session.setSessionToken(UUID.randomUUID().toString());
        session.setExpiresAt(LocalDateTime.now().plusHours(1));
        sessionService.save(session);

        return ResponseEntity.ok(session.getSessionToken());
    }

    @GetMapping("/getUser")
    public ResponseEntity<?> getUserInfo(@RequestHeader("Authorization") String token) {
        SessionModel session = sessionService.findBySessionToken(token);
        if (session == null || session.getExpiresAt().isBefore(LocalDateTime.now())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token.");
        }
        Long user = session.getUser();
        return ResponseEntity.ok(user);
    }
}
