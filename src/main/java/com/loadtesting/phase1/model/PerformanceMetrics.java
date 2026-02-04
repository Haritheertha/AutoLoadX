package com.loadtesting.phase1.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Phase 1: Performance Metrics Model
 * Holds raw performance data collected during load testing
 */
public class PerformanceMetrics {
    private final List<RequestResult> requestResults;
    private final LocalDateTime testStartTime;
    private LocalDateTime testEndTime;
    private int totalRequests;
    private int successfulRequests;
    private int failedRequests;
    private long minResponseTime;
    private long maxResponseTime;
    private double averageResponseTime;
    private double throughputPerSecond;
    
    public PerformanceMetrics() {
        this.requestResults = new CopyOnWriteArrayList<>();
        this.testStartTime = LocalDateTime.now();
        this.minResponseTime = Long.MAX_VALUE;
        this.maxResponseTime = 0;
    }
    
    public void addRequestResult(RequestResult result) {
        requestResults.add(result);
        updateMetrics();
    }
    
    private void updateMetrics() {
        totalRequests = requestResults.size();
        successfulRequests = (int) requestResults.stream().mapToLong(r -> r.isSuccess() ? 1 : 0).sum();
        failedRequests = totalRequests - successfulRequests;
        
        if (!requestResults.isEmpty()) {
            minResponseTime = requestResults.stream().mapToLong(RequestResult::getResponseTimeMs).min().orElse(0);
            maxResponseTime = requestResults.stream().mapToLong(RequestResult::getResponseTimeMs).max().orElse(0);
            averageResponseTime = requestResults.stream().mapToLong(RequestResult::getResponseTimeMs).average().orElse(0);
        }
    }
    
    public void finalizeMetrics() {
        this.testEndTime = LocalDateTime.now();
        updateMetrics();
        calculateThroughput();
    }
    
    private void calculateThroughput() {
        if (testStartTime != null && testEndTime != null) {
            long durationSeconds = java.time.Duration.between(testStartTime, testEndTime).getSeconds();
            if (durationSeconds > 0) {
                throughputPerSecond = (double) totalRequests / durationSeconds;
            }
        }
    }
    
    // Getters
    public List<RequestResult> getRequestResults() { return requestResults; }
    public LocalDateTime getTestStartTime() { return testStartTime; }
    public LocalDateTime getTestEndTime() { return testEndTime; }
    public int getTotalRequests() { return totalRequests; }
    public int getSuccessfulRequests() { return successfulRequests; }
    public int getFailedRequests() { return failedRequests; }
    public long getMinResponseTime() { return minResponseTime; }
    public long getMaxResponseTime() { return maxResponseTime; }
    public double getAverageResponseTime() { return averageResponseTime; }
    public double getThroughputPerSecond() { return throughputPerSecond; }
    public double getErrorRate() { return totalRequests > 0 ? (double) failedRequests / totalRequests * 100 : 0; }
}