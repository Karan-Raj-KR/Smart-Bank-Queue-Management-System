package com.bank.queue.controller;

import com.bank.queue.dto.TokenGenerateRequest;
import com.bank.queue.dto.TokenResponse;
import com.bank.queue.service.TokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/token")
@RequiredArgsConstructor
public class TokenController {

    private final TokenService tokenService;

    @PostMapping("/generate")
    public ResponseEntity<TokenResponse> generateToken(@Valid @RequestBody TokenGenerateRequest request) {
        return ResponseEntity.ok(tokenService.generateToken(request));
    }
}
