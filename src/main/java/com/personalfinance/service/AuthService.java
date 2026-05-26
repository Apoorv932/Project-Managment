package com.personalfinance.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.personalfinance.dto.LoginRequest;
import com.personalfinance.dto.RegisterRequest;
import com.personalfinance.dto.RegisterResponse;
import com.personalfinance.entity.UserEntity;
import com.personalfinance.exception.ConflictException;
import com.personalfinance.exception.NotFoundException;
import com.personalfinance.repository.UserRepository;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        String username = request.username().trim().toLowerCase();
        if (userRepository.existsByUsername(username)) {
            throw new ConflictException("Username already exists");
        }

        UserEntity user = new UserEntity(
                username,
                passwordEncoder.encode(request.password()),
                request.fullName().trim(),
                request.phoneNumber().trim());
        UserEntity savedUser = userRepository.save(user);

        return new RegisterResponse("User registered successfully", savedUser.getId());
    }

    @Transactional(readOnly = true)
    public UserEntity authenticate(LoginRequest request) {
        String username = request.username().trim().toLowerCase();
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("Invalid username or password"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new NotFoundException("Invalid username or password");
        }

        return user;
    }
}
