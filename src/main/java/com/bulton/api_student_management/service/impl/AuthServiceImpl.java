package com.bulton.api_student_management.service.impl;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bulton.api_student_management.dto.request.LoginRequest;
import com.bulton.api_student_management.dto.request.RegisterRequest;
import com.bulton.api_student_management.dto.response.AuthResponse;
import com.bulton.api_student_management.dto.response.UserResponse;
import com.bulton.api_student_management.entity.Role;
import com.bulton.api_student_management.entity.User;
import com.bulton.api_student_management.exception.DuplicationResourceException;
import com.bulton.api_student_management.repository.UserRepository;
import com.bulton.api_student_management.security.JwtService;
import com.bulton.api_student_management.service.AuthService;
import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
@Transactional 
public class AuthServiceImpl implements AuthService{
    private final UserRepository userRepository; 
    private final PasswordEncoder passwordEncoder; 
    private final AuthenticationManager authenticationManager; 
    private final JwtService jwtService; 
    @Override
    public UserResponse register(RegisterRequest request) {
        String username = request.getUsername().trim(); 
        String email = request.getEmail().trim().toLowerCase();
        if( userRepository.existsByUsername(username)){
            throw new DuplicationResourceException(
                "User Already exits" + username
            ); 
        }
        if ( userRepository.existsByEmail(email)){
            throw new DuplicationResourceException(
                "Email Already Exits" + email
            );
        } 
        User user = User.builder()
            .username(username)
            .email(email)
            .password(passwordEncoder.encode(request.getPassword()))
            .role(Role.USER)
            .enabled(true)
            .build();
        User savedUser = userRepository.save(user);
        return UserResponse.fromEntity(savedUser);
    }
    @Override
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = 
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getUsername(),
                    request.getPassword())
            );
        String token = jwtService.generateToken(authentication); 
        String role = authentication.getAuthorities()
            .stream()
            .findFirst()
            .map(authory -> authory.getAuthority())
            .orElse("ROLE_USER"); 
        return AuthResponse.builder()
            .accessToken(token)
            .tokenType("Bearer")
            .expiresIn(jwtService.getExpiration())
            .username(authentication.getName())
            .role(role)
            .build();
    }

}
