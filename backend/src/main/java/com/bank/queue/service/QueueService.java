package com.bank.queue.service;

import com.bank.queue.dto.QueueStatusResponse;
import com.bank.queue.model.Token;
import com.bank.queue.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class QueueService {

    private final TokenRepository tokenRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final org.springframework.data.redis.core.RedisTemplate<String, Object> redisTemplate;

    private static final String QUEUE_STATUS_KEY = "queue:status:branch:";

    public Token callNext(Long branchId, Integer counterNumber) {
        List<Token> waitingTokens = tokenRepository.findByBranchIdAndStatusOrderByCreatedAtAsc(branchId, "WAITING");
        
        if (waitingTokens.isEmpty()) {
            throw new RuntimeException("No tokens waiting in the queue.");
        }

        Token nextToken = waitingTokens.get(0);
        nextToken.setStatus("CALLED");
        nextToken.setCalledAt(LocalDateTime.now());
        
        Token savedToken = tokenRepository.save(nextToken);
        
        // Invalidate/Update Cache
        updateQueueStatusCache(branchId);
        
        publishEvent("TOKEN_CALLED", savedToken.getTokenNumber(), counterNumber, branchId);
        return savedToken;
    }

    public Token skipToken(String tokenNumber, Integer counterNumber, Long branchId) {
        Token token = tokenRepository.findByTokenNumber(tokenNumber);
        if (token == null || !token.getStatus().equals("CALLED")) {
            throw new RuntimeException("Token cannot be skipped at this state.");
        }
        token.setStatus("SKIPPED");
        Token savedToken = tokenRepository.save(token);
        
        updateQueueStatusCache(branchId);
        
        publishEvent("TOKEN_SKIPPED", tokenNumber, counterNumber, branchId);
        return savedToken;
    }

    public Token serveToken(String tokenNumber, Integer counterNumber, Long branchId) {
        Token token = tokenRepository.findByTokenNumber(tokenNumber);
        if (token == null || !token.getStatus().equals("CALLED")) {
            throw new RuntimeException("Token cannot be served at this state.");
        }
        token.setStatus("SERVED");
        token.setServedAt(LocalDateTime.now());
        Token savedToken = tokenRepository.save(token);
        
        updateQueueStatusCache(branchId);
        
        publishEvent("TOKEN_SERVED", tokenNumber, counterNumber, branchId);
        return savedToken;
    }

    public QueueStatusResponse getQueueStatus(Long branchId) {
        // Try to get from Cache
        Object cached = redisTemplate.opsForValue().get(QUEUE_STATUS_KEY + branchId);
        if (cached instanceof QueueStatusResponse) {
            return (QueueStatusResponse) cached;
        }

        // Fallback to DB
        List<Token> waitingTokens = tokenRepository.findByBranchIdAndStatusOrderByCreatedAtAsc(branchId, "WAITING");
        List<Token> calledTokens = tokenRepository.findByBranchIdAndStatusOrderByCreatedAtAsc(branchId, "CALLED");

        String currentToken = calledTokens.isEmpty() ? null : calledTokens.get(calledTokens.size() - 1).getTokenNumber();
        String nextToken = waitingTokens.isEmpty() ? null : waitingTokens.get(0).getTokenNumber();
        long queueLength = waitingTokens.size();

        QueueStatusResponse response = new QueueStatusResponse(currentToken, nextToken, queueLength);
        
        // Save to Cache
        redisTemplate.opsForValue().set(QUEUE_STATUS_KEY + branchId, response);
        
        return response;
    }

    private void updateQueueStatusCache(Long branchId) {
        // Simple invalidation or refresh
        redisTemplate.delete(QUEUE_STATUS_KEY + branchId);
    }

    private void publishEvent(String eventType, String tokenNumber, Integer counterNumber, Long branchId) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("event", eventType);
        payload.put("token", tokenNumber);
        payload.put("counter", counterNumber);
        
        messagingTemplate.convertAndSend("/topic/branch/" + branchId + "/queue-updates", (Object) payload);
    }
}
