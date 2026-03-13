package com.bank.queue.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QueueStatusResponse {
    private String currentToken;
    private String nextToken;
    private long queueLength;
}
