package com.bank.queue.controller;

import com.bank.queue.dto.QueueStatusResponse;
import com.bank.queue.model.Token;
import com.bank.queue.service.QueueService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/queue")
@RequiredArgsConstructor
public class QueueController {

    private final QueueService queueService;

    @GetMapping("/status")
    public ResponseEntity<QueueStatusResponse> getStatus(@RequestParam Long branchId) {
        return ResponseEntity.ok(queueService.getQueueStatus(branchId));
    }

    @PostMapping("/call-next")
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<Token> callNext(@RequestParam Long branchId, @RequestParam Integer counterNumber) {
        return ResponseEntity.ok(queueService.callNext(branchId, counterNumber));
    }

    @PostMapping("/skip")
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<Token> skipToken(@RequestParam String tokenNumber, 
                                           @RequestParam Integer counterNumber,
                                           @RequestParam Long branchId) {
        return ResponseEntity.ok(queueService.skipToken(tokenNumber, counterNumber, branchId));
    }

    @PostMapping("/serve")
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<Token> serveToken(@RequestParam String tokenNumber, 
                                            @RequestParam Integer counterNumber,
                                            @RequestParam Long branchId) {
        return ResponseEntity.ok(queueService.serveToken(tokenNumber, counterNumber, branchId));
    }
}
