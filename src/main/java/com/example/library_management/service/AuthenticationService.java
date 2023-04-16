package com.example.library_management.service;


import com.example.library_management.DTO.AuthenticationRequest;
import com.example.library_management.DTO.AuthenticationResponse;
import com.example.library_management.DTO.RegisterRequest;
import com.example.library_management.config.ApplicationConfig;
import com.example.library_management.config.JwtService;
import com.example.library_management.entity.BannedUser;
import com.example.library_management.entity.User;
import com.example.library_management.repository.BanList;
import com.example.library_management.repository.RoleRepository;
import com.example.library_management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AuthenticationService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JwtService jwtService;
    private final ApplicationConfig applicationConfig;
    private final BanList banList;

    public AuthenticationResponse register(RegisterRequest request) {
        log.info(request.toString());
        if (userRepository.findByEmail(request.getEmail()).isPresent()){
            throw new RuntimeException("user already exists");
        }
        isUserBanned(request.getEmail());

        User newUser = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(applicationConfig.passwordEncoder().encode(request.getPassword()))
                .roles(Collections.singletonList(roleRepository.findByName("USER").get()))
                .build();
        log.info(newUser.getAuthorities().toString());
        log.info("SHIT "+newUser.getAuthorities().toString());
        userRepository.save(newUser);
        String jwtToken = jwtService.generateToken(newUser);

        return AuthenticationResponse.builder().token(jwtToken).authorisations(newUser.getRoles().stream().map(role -> role.toString()).collect(Collectors.toList())).build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        log.info(request.toString());
        isUserBanned(request.getEmail());
        applicationConfig.authenticationProvider().authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(),request.getPassword())
        );
        log.info(request.toString());
        log.info(userRepository.findByEmail(request.getEmail()).orElseThrow(()-> new RuntimeException()).getFirstName());
        var user = userRepository.findByEmail(request.getEmail()).orElseThrow(()-> new IllegalArgumentException("not found"));
        var jwtToken = jwtService.generateToken(user);

        return AuthenticationResponse.builder().token(jwtToken).authorisations(user.getRoles().stream().map(role -> role.toString()).collect(Collectors.toList())).build();
    }

    public void isUserBanned(String email) {
        Optional<BannedUser> exist = banList.findBannedUserByUserEmail(email);
        if (exist.isPresent()) throw new RuntimeException("this email permanently banned");
    }

    @Transactional(readOnly = true)
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info(authentication.getName()+" hcuibhjknabhv hibjociyv hjo");
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            return userRepository.findByEmail(authentication.getName()).orElseThrow(()-> new RuntimeException("No user with this email"));
        }else{
            throw new RuntimeException("the jwt is incomplete");
        }
    }


}
