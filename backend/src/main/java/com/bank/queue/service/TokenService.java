package com.bank.queue.service;

import com.bank.queue.dto.TokenGenerateRequest;
import com.bank.queue.dto.TokenResponse;
import com.bank.queue.model.Token;
import com.bank.queue.repository.BranchRepository;
import com.bank.queue.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final TokenRepository tokenRepository;
    private final BranchRepository branchRepository;
    private final org.springframework.data.redis.core.RedisTemplate<String, Object> redisTemplate;

    private static final String TOKEN_KEY_PREFIX = "branch:token:counter:";

    public TokenResponse generateToken(TokenGenerateRequest request) {
        Long branchId = Objects.requireNonNull(request.getBranchId(), "branchId must not be null");
        
        if (!branchRepository.existsById(branchId)) {
            throw new RuntimeException("Branch does not exist with id: " + branchId);
        }

        // Use Redis to increment token counter for the branch
        String redisKey = TOKEN_KEY_PREFIX + branchId;
        Long nextTokenSeq = redisTemplate.opsForValue().increment(redisKey);
        
        // Count existing waiting tokens for position in real-time
        long waitingTokensCount = tokenRepository.countByBranchIdAndStatus(branchId, "WAITING");
        int position = (int) waitingTokensCount + 1;

        // Generate token number like B1-001
        String tokenNumber = String.format("B%d-%03d", branchId, nextTokenSeq);

        Token token = new Token();
        token.setBranchId(branchId);
        token.setTokenNumber(tokenNumber);
        token.setStatus("WAITING");

        tokenRepository.save(token);

        String estimatedWaitTime = (position * 5) + " minutes";

        return new TokenResponse(tokenNumber, position, estimatedWaitTime);
    }
}
