package com.bank.queue.controller;

import com.bank.queue.model.Branch;
import com.bank.queue.service.BranchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/branch")
@RequiredArgsConstructor
public class BranchController {

    private final BranchService branchService;

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Branch> createBranch(@RequestBody Branch branch) {
        return ResponseEntity.ok(branchService.createBranch(branch));
    }

    @GetMapping("/all")
    public ResponseEntity<List<Branch>> getAllBranches() {
        return ResponseEntity.ok(branchService.getAllBranches());
    }
}
