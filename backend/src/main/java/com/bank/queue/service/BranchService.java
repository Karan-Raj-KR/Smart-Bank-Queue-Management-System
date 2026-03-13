package com.bank.queue.service;

import com.bank.queue.model.Branch;
import com.bank.queue.repository.BranchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class BranchService {

    private final BranchRepository branchRepository;

    public Branch createBranch(Branch branch) {
        return branchRepository.save(Objects.requireNonNull(branch, "branch must not be null"));
    }

    public List<Branch> getAllBranches() {
        return branchRepository.findAll();
    }

    public Branch getBranchById(Long id) {
        return branchRepository.findById(Objects.requireNonNull(id, "id must not be null"))
                .orElseThrow(() -> new RuntimeException("Branch not found with id: " + id));
    }

    public Branch updateBranch(Long id, Branch updatedBranch) {
        Branch branch = getBranchById(id);
        branch.setName(updatedBranch.getName());
        branch.setLocation(updatedBranch.getLocation());
        return branchRepository.save(branch);
    }

    public void deleteBranch(Long id) {
        branchRepository.deleteById(Objects.requireNonNull(id, "id must not be null"));
    }
}
