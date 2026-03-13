package com.bank.queue.service;

import com.bank.queue.model.Counter;
import com.bank.queue.repository.BranchRepository;
import com.bank.queue.repository.CounterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CounterService {

    private final CounterRepository counterRepository;
    private final BranchRepository branchRepository;

    public Counter createCounter(Counter counter) {
        Objects.requireNonNull(counter, "counter must not be null");
        Long branchId = Objects.requireNonNull(counter.getBranchId(), "branchId must not be null");
        if (!branchRepository.existsById(branchId)) {
            throw new RuntimeException("Branch does not exist with id: " + branchId);
        }
        counter.setStatus("ACTIVE"); // Default status
        return counterRepository.save(counter);
    }

    public List<Counter> getCountersByBranch(Long branchId) {
        return counterRepository.findByBranchId(branchId);
    }

    public Counter getCounterById(Long id) {
        return counterRepository.findById(Objects.requireNonNull(id, "id must not be null"))
                .orElseThrow(() -> new RuntimeException("Counter not found with id: " + id));
    }

    public Counter updateCounterStatus(Long id, String status) {
        Counter counter = getCounterById(id);
        counter.setStatus(status.toUpperCase());
        return counterRepository.save(counter);
    }
}
