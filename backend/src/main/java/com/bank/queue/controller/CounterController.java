package com.bank.queue.controller;

import com.bank.queue.model.Counter;
import com.bank.queue.service.CounterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/counter")
@RequiredArgsConstructor
public class CounterController {

    private final CounterService counterService;

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Counter> createCounter(@RequestBody Counter counter) {
        return ResponseEntity.ok(counterService.createCounter(counter));
    }

    @GetMapping("/branch/{branchId}")
    public ResponseEntity<List<Counter>> getCounters(@PathVariable Long branchId) {
        return ResponseEntity.ok(counterService.getCountersByBranch(branchId));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<Counter> updateStatus(@PathVariable Long id, @RequestParam String status) {
        return ResponseEntity.ok(counterService.updateCounterStatus(id, status));
    }
}
