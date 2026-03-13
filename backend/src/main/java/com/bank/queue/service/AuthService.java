package com.bank.queue.service;

import com.bank.queue.dto.AuthRequest;
import com.bank.queue.dto.AuthResponse;
import com.bank.queue.dto.RegisterRequest;
import com.bank.queue.model.User;
import com.bank.queue.repository.UserRepository;
import com.bank.queue.security.CustomUserDetailsService;
import com.bank.queue.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import java.util.Objects;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email is already taken!");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole().toUpperCase());
        user.setBranchId(request.getBranchId());

        userRepository.save(user);

        UserDetails userDetails = userDetailsService.loadUserByUsername(Objects.requireNonNull(user.getEmail()));
        String jwtToken = jwtUtils.generateToken(userDetails);
        
        return new AuthResponse(jwtToken);
    }

    public AuthResponse login(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(Objects.requireNonNull(request.getEmail()));
        String jwtToken = jwtUtils.generateToken(userDetails);

        return new AuthResponse(jwtToken);
    }
}
