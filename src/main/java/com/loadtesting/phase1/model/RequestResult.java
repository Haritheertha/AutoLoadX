package com.loadtesting.phase1.model;

import java.time.LocalDateTime;

/**
 * Phase 1: Request Result
 * Captures data from individual HTTP requests
 */
public class RequestResult {
    private final LocalDateTime timestamp;
    private final long responseTimeMs;
    private final int statusCode;
    private final boolean success;
    private final String errorMessage;
    private final int threadId;
    
    public RequestResult(long responseTimeMs, int statusCode, boolean success, String errorMessage, int threadId) {
        this.timestamp = LocalDateTime.now();
        this.responseTimeMs = responseTimeMs;
        this.statusCode = statusCode;
        this.success = success;
        this.errorMessage = errorMessage;
        this.threadId = threadId;
    }
    
    // Getters
    public LocalDateTime getTimestamp() { return timestamp; }
    public long getResponseTimeMs() { return responseTimeMs; }
    public int getStatusCode() { return statusCode; }
    public boolean isSuccess() { return success; }
    public String getErrorMessage() { return errorMessage; }
    public int getThreadId() { return threadId; }
}