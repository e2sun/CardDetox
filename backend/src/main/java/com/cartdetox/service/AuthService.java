package com.cartdetox.service;

import com.cartdetox.dto.*;
import com.cartdetox.model.User;
import com.cartdetox.repository.UserRepository;
import com.cartdetox.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthResponse register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.getEmail()))
            throw new RuntimeException("Email already in use");
        if (userRepository.existsByUsername(req.getUsername()))
            throw new RuntimeException("Username already taken");

        User user = User.builder()
                .username(req.getUsername())
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .detoxTokens(1000.0)
                .build();
        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getEmail());
        return buildResponse(user, token);
    }

    public AuthResponse login(LoginRequest req) {
        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));
        if (!passwordEncoder.matches(req.getPassword(), user.getPassword()))
            throw new RuntimeException("Invalid credentials");

        String token = jwtUtil.generateToken(user.getEmail());
        return buildResponse(user, token);
    }

    public AuthResponse getMe(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return buildResponse(user, null);
    }

    private AuthResponse buildResponse(User user, String token) {
        boolean spinAvailable = user.getLastSpinDate() == null ||
                !user.getLastSpinDate().equals(LocalDate.now());
        return AuthResponse.builder()
                .token(token)
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .detoxTokens(user.getDetoxTokens())
                .spinAvailable(spinAvailable)
                .totalOrders(user.getTotalOrders())
                .totalSaved(user.getTotalSaved())
                .build();
    }
}
