package com.loadtesting.phase1.service;

import com.loadtesting.phase1.model.PerformanceMetrics;
import com.loadtesting.phase1.model.RequestResult;

/**
 * Phase 1: Metrics Collector
 * Thread-safe collection of performance metrics
 */
public class MetricsCollector {
    private final PerformanceMetrics metrics;
    
    public MetricsCollector() {
        this.metrics = new PerformanceMetrics();
    }
    
    public synchronized void collectRequestResult(RequestResult result) {
        metrics.addRequestResult(result);
    }
    
    public synchronized PerformanceMetrics getMetrics() {
        return metrics;
    }
    
    public synchronized void finalizeCollection() {
        metrics.finalizeMetrics();
    }
    
    public synchronized void printCurrentStats() {
        System.out.printf("Requests: %d | Success: %d | Failed: %d | Avg Response: %.2f ms%n",
            metrics.getTotalRequests(),
            metrics.getSuccessfulRequests(),
            metrics.getFailedRequests(),
            metrics.getAverageResponseTime());
    }
}