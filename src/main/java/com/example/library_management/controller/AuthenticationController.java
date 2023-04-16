package com.example.library_management.controller;

import com.example.library_management.DTO.AuthenticationRequest;
import com.example.library_management.DTO.AuthenticationResponse;
import com.example.library_management.DTO.RegisterRequest;
import com.example.library_management.entity.User;
import com.example.library_management.repository.UserRepository;
import com.example.library_management.service.AuthenticationService;
import com.example.library_management.service.BookService;
import com.example.library_management.service.OrderService;
import com.example.library_management.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/lib/auth")
@RequiredArgsConstructor
@CrossOrigin("*")
@Slf4j
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final UserRepository userRepository;
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request){
        log.info("hi my name is jefffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff");
        return ResponseEntity.ok(authenticationService.register(request));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody AuthenticationRequest request){
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }
    
    @GetMapping("/current")
    public  ResponseEntity<User> getCurrentUser() {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(authenticationService.getCurrentUser());
    }

}
