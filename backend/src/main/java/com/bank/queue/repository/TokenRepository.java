package com.bank.queue.repository;

import com.bank.queue.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
    List<Token> findByBranchId(Long branchId);
    List<Token> findByBranchIdAndStatusOrderByCreatedAtAsc(Long branchId, String status);
    long countByBranchIdAndStatus(Long branchId, String status);
    
    // Custom query to find token by its B102 format number
    Token findByTokenNumber(String tokenNumber);
}
