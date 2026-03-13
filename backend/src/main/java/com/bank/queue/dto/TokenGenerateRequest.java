package com.bank.queue.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenGenerateRequest {

    @NotNull(message = "Branch ID is required")
    private Long branchId;
}
