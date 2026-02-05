package com.loadtesting.phase1.service;

import com.loadtesting.phase1.model.PerformanceMetrics;
import com.loadtesting.phase1.model.RequestResult;
import com.loadtesting.phase1.model.TestConfiguration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Phase 1: Load Test Executor
 * Main execution engine with multithreading support
 */
public class LoadTestExecutor {
    private final HttpRequestExecutor requestExecutor;
    private final MetricsCollector metricsCollector;
    private final AtomicBoolean running;
    private final AtomicInteger activeThreads;
    
    public LoadTestExecutor() {
        this.requestExecutor = new HttpRequestExecutor();
        this.metricsCollector = new MetricsCollector();
        this.running = new AtomicBoolean(false);
        this.activeThreads = new AtomicInteger(0);
    }
    
    public PerformanceMetrics executeLoadTest(TestConfiguration config) {
        if (!config.isValid()) {
            throw new IllegalArgumentException("Invalid test configuration");
        }
        
        System.out.println("=== AutoLoadX - Starting Load Test ===");
        System.out.println("Endpoint: " + config.getApiEndpoint());
        System.out.println("Method: " + config.getHttpMethod());
        System.out.println("Users: " + config.getConcurrentUsers());
        System.out.println("Duration: " + config.getTestDurationSeconds() + "s");
        System.out.println();
        
        running.set(true);
        ExecutorService executorService = Executors.newFixedThreadPool(Math.min(config.getConcurrentUsers(), 1000));
        
        // Start monitoring thread
        Thread monitorThread = new Thread(() -> monitorProgress());
        monitorThread.start();
        
        // Execute ramp-up
        executeRampUp(config, executorService);
        
        // Execute main test
        executeMainTest(config, executorService);
        
        // Shutdown
        running.set(false);
        shutdownExecutor(executorService);
        
        try {
            monitorThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        metricsCollector.finalizeCollection();
        printFinalResults();
        
        return metricsCollector.getMetrics();
    }
    
    private void executeRampUp(TestConfiguration config, ExecutorService executorService) {
        int rampUpUsers = config.getConcurrentUsers();
        int rampUpDuration = config.getRampUpSeconds();
        
        if (rampUpDuration <= 0) {
            // No ramp-up, start all users immediately
            for (int i = 0; i < rampUpUsers; i++) {
                startUserThread(config, executorService, i);
            }
            return;
        }
        
        // Gradual ramp-up
        long delayBetweenUsers = (rampUpDuration * 1000L) / rampUpUsers;
        
        for (int i = 0; i < rampUpUsers; i++) {
            if (!running.get()) break;
            
            startUserThread(config, executorService, i);
            
            try {
                Thread.sleep(delayBetweenUsers);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
    
    private void executeMainTest(TestConfiguration config, ExecutorService executorService) {
        long testEndTime = System.currentTimeMillis() + (config.getTestDurationSeconds() * 1000L);
        
        while (running.get() && System.currentTimeMillis() < testEndTime) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
    
    private void startUserThread(TestConfiguration config, ExecutorService executorService, int userId) {
        executorService.submit(() -> {
            activeThreads.incrementAndGet();
            
            while (running.get()) {
                RequestResult result = requestExecutor.executeRequest(config, userId);
                metricsCollector.collectRequestResult(result);
                
                // Small delay between requests from same user
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            
            activeThreads.decrementAndGet();
        });
    }
    
    private void monitorProgress() {
        while (running.get()) {
            try {
                Thread.sleep(5000); // Print stats every 5 seconds
                if (running.get()) {
                    metricsCollector.printCurrentStats();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
    
    private void shutdownExecutor(ExecutorService executorService) {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(10, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
    
    private void printFinalResults() {
        PerformanceMetrics metrics = metricsCollector.getMetrics();
        
        System.out.println();
        System.out.println("=== AutoLoadX - LOAD TEST COMPLETED ===");
        System.out.println("Total Requests: " + metrics.getTotalRequests());
        System.out.println("Successful: " + metrics.getSuccessfulRequests());
        System.out.println("Failed: " + metrics.getFailedRequests());
        System.out.println("Error Rate: " + String.format("%.4f%%", metrics.getErrorRate()));
        System.out.println("Min Response Time: " + metrics.getMinResponseTime() + " ms");
        System.out.println("Max Response Time: " + metrics.getMaxResponseTime() + " ms");
        System.out.println("Avg Response Time: " + String.format("%.2f ms", metrics.getAverageResponseTime()));
        System.out.println("Throughput: " + String.format("%.2f req/s", metrics.getThroughputPerSecond()));
    }
}