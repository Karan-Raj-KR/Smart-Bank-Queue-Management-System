package com.bank.queue.service;

import com.bank.queue.model.Branch;
import com.bank.queue.repository.BranchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BranchService {

    private final BranchRepository branchRepository;

    public Branch createBranch(@NonNull Branch branch) {
        return branchRepository.save(branch);
    }

    public List<Branch> getAllBranches() {
        return branchRepository.findAll();
    }

    public Branch getBranchById(@NonNull Long id) {
        return branchRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Branch not found with id: " + id));
    }
    
    public Branch updateBranch(@NonNull Long id, Branch updatedBranch) {
        Branch branch = getBranchById(id);
        branch.setName(updatedBranch.getName());
        branch.setLocation(updatedBranch.getLocation());
        return branchRepository.save(branch);
    }

    public void deleteBranch(@NonNull Long id) {
        branchRepository.deleteById(id);
    }
}
