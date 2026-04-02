package com.example.teste_tinnova_java.controllers;

import com.example.teste_tinnova_java.domain.user.User;
import com.example.teste_tinnova_java.domain.user.UserRole;
import com.example.teste_tinnova_java.dto.LoginRequestDTO;
import com.example.teste_tinnova_java.dto.LoginResponseDTO;
import com.example.teste_tinnova_java.dto.RegisterRequestDTO;
import com.example.teste_tinnova_java.infra.security.TokenService;
import com.example.teste_tinnova_java.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody LoginRequestDTO body) {
        User user = this.userRepository.findByEmail(body.email()).orElseThrow(() -> new RuntimeException("User not found"));
        if(passwordEncoder.matches(body.password(), user.getPassword())) {
            String token = this.tokenService.generateToken(user);
            return ResponseEntity.ok(new LoginResponseDTO(user.getEmail(), token));
        }
        return ResponseEntity.status(401).build();
    }

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody RegisterRequestDTO body) {
        Optional<User> user = this.userRepository.findByEmail(body.email());
        if(user.isPresent()) {
           return ResponseEntity.status(409).build();
        }

        User newUser = new User();
        newUser.setEmail(body.email());
        newUser.setPassword(this.passwordEncoder.encode(body.password()));
        UserRole role = body.role() != null ? UserRole.valueOf(body.role().toUpperCase()) : UserRole.USER;
        newUser.setRole(role);
        this.userRepository.save(newUser);

        String token = this.tokenService.generateToken(newUser);

        return ResponseEntity.ok(new LoginResponseDTO(newUser.getEmail(), token));
    }
}
