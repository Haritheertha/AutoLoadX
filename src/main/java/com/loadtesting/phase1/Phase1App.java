package com.loadtesting.phase1;

import com.loadtesting.phase1.model.PerformanceMetrics;
import com.loadtesting.phase1.model.TestConfiguration;
import com.loadtesting.phase1.service.LoadTestExecutor;

import java.util.Scanner;

/**
 * Phase 1: Main Application
 * Simple console interface for load testing
 */
public class Phase1App {
    
    public static void main(String[] args) {
        System.out.println("=== AutoLoadX - Performance Testing Tool ===");
        System.out.println("Minimal load testing with raw metrics collection");
        System.out.println();
        
        TestConfiguration config = getTestConfiguration();
        
        if (!config.isValid()) {
            System.out.println("Invalid configuration. Exiting.");
            return;
        }
        
        LoadTestExecutor executor = new LoadTestExecutor();
        PerformanceMetrics results = executor.executeLoadTest(config);
        
        // Results are already printed by executor
        // Raw metrics are available in 'results' object for Phase 2 extensions
        
        System.out.println();
        System.out.println("Raw metrics object ready for Phase 2 analysis and reporting.");
    }
    
    private static TestConfiguration getTestConfiguration() {
        Scanner scanner = new Scanner(System.in);
        TestConfiguration config = new TestConfiguration();
        
        System.out.print("API Endpoint: ");
        config.setApiEndpoint(scanner.nextLine().trim());
        
        System.out.print("HTTP Method (GET/POST/PUT/DELETE) [GET]: ");
        String method = scanner.nextLine().trim();
        if (!method.isEmpty()) {
            config.setHttpMethod(method);
        }
        
        System.out.print("Request Body (optional): ");
        String body = scanner.nextLine().trim();
        if (!body.isEmpty()) {
            config.setRequestBody(body);
        }
        
        System.out.print("Concurrent Users [10]: ");
        String users = scanner.nextLine().trim();
        if (!users.isEmpty()) {
            try {
                config.setConcurrentUsers(Integer.parseInt(users));
            } catch (NumberFormatException e) {
                System.out.println("Invalid number, using default: 10");
            }
        }
        
        System.out.print("Test Duration (seconds) [60]: ");
        String duration = scanner.nextLine().trim();
        if (!duration.isEmpty()) {
            try {
                config.setTestDurationSeconds(Integer.parseInt(duration));
            } catch (NumberFormatException e) {
                System.out.println("Invalid number, using default: 60");
            }
        }
        
        System.out.print("Ramp-up Duration (seconds) [10]: ");
        String rampUp = scanner.nextLine().trim();
        if (!rampUp.isEmpty()) {
            try {
                config.setRampUpSeconds(Integer.parseInt(rampUp));
            } catch (NumberFormatException e) {
                System.out.println("Invalid number, using default: 10");
            }
        }
        
        scanner.close();
        return config;
    }
}