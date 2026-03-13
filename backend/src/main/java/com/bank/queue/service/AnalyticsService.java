package com.bank.queue.service;

import com.bank.queue.model.Token;
import com.bank.queue.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final TokenRepository tokenRepository;

    public Map<String, Object> getBranchAnalytics(Long branchId) {
        List<Token> allBranchTokens = tokenRepository.findByBranchId(branchId);
        
        long totalTokens = allBranchTokens.size();
        long servedTokens = allBranchTokens.stream().filter(t -> t.getStatus().equals("SERVED")).count();
        long skippedTokens = allBranchTokens.stream().filter(t -> t.getStatus().equals("SKIPPED")).count();

        long totalWaitTimeMillis = allBranchTokens.stream()
                .filter(t -> t.getStatus().equals("SERVED") && t.getCalledAt() != null)
                .mapToLong(t -> Duration.between(t.getCreatedAt(), t.getCalledAt()).toMillis())
                .sum();

        long averageWaitTimeMinutes = servedTokens == 0 ? 0 : (totalWaitTimeMillis / servedTokens) / 60000;

        Map<String, Object> analytics = new HashMap<>();
        analytics.put("totalCustomersGenerated", totalTokens);
        analytics.put("totalCustomersServed", servedTokens);
        analytics.put("totalCustomersSkipped", skippedTokens);
        analytics.put("averageWaitTimeMinutes", averageWaitTimeMinutes);
        
        return analytics;
    }
}
