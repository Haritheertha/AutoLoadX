package com.loadtesting.phase1.service;

import com.loadtesting.phase1.model.PerformanceMetrics;
import com.loadtesting.phase1.model.TestConfiguration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Advanced Scalability Testing Strategy
 * Implements comprehensive load testing with detailed analysis
 */
public class ScalabilityTester {
    
    private final LoadTestExecutor executor;
    private final List<ScalabilityResult> results;
    
    // Test configuration
    private static final int[] USER_LEVELS = {10, 100, 500, 1000, 5000, 10000};
    private static final int TEST_DURATION = 300; // 5 minutes per level
    private static final int STABILIZATION_TIME = 60; // 1 minute stabilization
    
    // Performance thresholds
    private static final double MAX_ERROR_RATE = 5.0; // 5%
    private static final long MAX_RESPONSE_TIME = 5000; // 5 seconds
    private static final double MIN_THROUGHPUT_RATIO = 0.7; // 70% of expected
    
    public ScalabilityTester() {
        this.executor = new LoadTestExecutor();
        this.results = new ArrayList<>();
    }
    
    public ScalabilityTestReport executeScalabilityTest(String endpoint, String method, String body) {
        System.out.println("🚀 === AutoLoadX Scalability Testing Strategy ===");
        System.out.println("Target: " + endpoint);
        System.out.println("Method: " + method);
        System.out.println("Test Levels: " + Arrays.toString(USER_LEVELS));
        System.out.println("Duration per level: " + TEST_DURATION + "s");
        System.out.println("═══════════════════════════════════════════════════");
        System.out.println();
        
        ScalabilityTestReport report = new ScalabilityTestReport();
        report.startTime = LocalDateTime.now();
        report.endpoint = endpoint;
        
        for (int userLevel : USER_LEVELS) {
            System.out.println("📊 Testing with " + userLevel + " concurrent users...");
            
            ScalabilityResult result = executeLoadLevel(endpoint, method, body, userLevel);
            results.add(result);
            report.results.add(result);
            
            printLevelSummary(result);
            
            // Check if we should stop testing
            if (shouldStopTesting(result)) {
                System.out.println("⚠️ Stopping test - System instability detected");
                break;
            }
            
            // Wait between test levels
            if (userLevel != USER_LEVELS[USER_LEVELS.length - 1]) {
                System.out.println("⏳ Waiting " + STABILIZATION_TIME + "s for system stabilization...");
                sleep(STABILIZATION_TIME * 1000);
            }
        }
        
        report.endTime = LocalDateTime.now();
        analyzeScalabilityResults(report);
        
        return report;
    }
    
    private ScalabilityResult executeLoadLevel(String endpoint, String method, String body, int users) {
        TestConfiguration config = new TestConfiguration();
        config.setApiEndpoint(endpoint);
        config.setHttpMethod(method);
        config.setRequestBody(body);
        config.setConcurrentUsers(users);
        config.setTestDurationSeconds(TEST_DURATION);
        config.setRampUpSeconds(Math.min(users / 10, 60)); // Max 60s ramp-up
        
        PerformanceMetrics metrics = executor.executeLoadTest(config);
        
        return new ScalabilityResult(users, metrics);
    }
    
    private boolean shouldStopTesting(ScalabilityResult result) {
        return result.metrics.getErrorRate() > MAX_ERROR_RATE ||
               result.metrics.getAverageResponseTime() > MAX_RESPONSE_TIME ||
               result.metrics.getThroughputPerSecond() < 1.0;
    }
    
    private void printLevelSummary(ScalabilityResult result) {
        System.out.printf("  Users: %,d | Throughput: %.2f req/s | Avg Response: %.2f ms | Error Rate: %.4f%%\\n",
            result.userLevel,
            result.metrics.getThroughputPerSecond(),
            result.metrics.getAverageResponseTime(),
            result.metrics.getErrorRate());
        System.out.println();
    }
    
    private void analyzeScalabilityResults(ScalabilityTestReport report) {
        System.out.println("📈 === SCALABILITY ANALYSIS REPORT ===");
        System.out.println();
        
        // Find performance points
        ScalabilityResult stablePoint = findStableOperatingLoad();
        ScalabilityResult saturationPoint = findSaturationPoint();
        ScalabilityResult breakingPoint = findBreakingPoint();
        
        report.stableLoad = stablePoint;
        report.saturationPoint = saturationPoint;
        report.breakingPoint = breakingPoint;
        
        // Print analysis
        printPerformancePoints(stablePoint, saturationPoint, breakingPoint);
        printDetailedMetrics();
        printRecommendations(report);
    }
    
    private ScalabilityResult findStableOperatingLoad() {
        // Find highest load with error rate < 1% and response time < 2s
        return results.stream()
            .filter(r -> r.metrics.getErrorRate() < 1.0 && r.metrics.getAverageResponseTime() < 2000)
            .max(Comparator.comparing(r -> r.userLevel))
            .orElse(results.get(0));
    }
    
    private ScalabilityResult findSaturationPoint() {
        // Find point where throughput stops increasing proportionally
        ScalabilityResult maxThroughput = results.stream()
            .max(Comparator.comparing(r -> r.metrics.getThroughputPerSecond()))
            .orElse(null);
        
        if (maxThroughput == null) return null;
        
        // Find first point where throughput is 90% of max
        double threshold = maxThroughput.metrics.getThroughputPerSecond() * 0.9;
        return results.stream()
            .filter(r -> r.metrics.getThroughputPerSecond() >= threshold)
            .min(Comparator.comparing(r -> r.userLevel))
            .orElse(maxThroughput);
    }
    
    private ScalabilityResult findBreakingPoint() {
        // Find first point with high error rate or response time
        return results.stream()
            .filter(r -> r.metrics.getErrorRate() > MAX_ERROR_RATE || 
                        r.metrics.getAverageResponseTime() > MAX_RESPONSE_TIME)
            .min(Comparator.comparing(r -> r.userLevel))
            .orElse(null);
    }
    
    private void printPerformancePoints(ScalabilityResult stable, ScalabilityResult saturation, ScalabilityResult breaking) {
        System.out.println("🎯 PERFORMANCE ANALYSIS:");
        System.out.println("═══════════════════════════════════════");
        
        if (stable != null) {
            System.out.printf("✅ STABLE OPERATING LOAD: %,d users\\n", stable.userLevel);
            System.out.printf("   Throughput: %.2f req/s | Response: %.2f ms | Error: %.4f%%\\n\\n",
                stable.metrics.getThroughputPerSecond(),
                stable.metrics.getAverageResponseTime(),
                stable.metrics.getErrorRate());
        }
        
        if (saturation != null) {
            System.out.printf("⚠️ SATURATION POINT: %,d users\\n", saturation.userLevel);
            System.out.printf("   Throughput: %.2f req/s | Response: %.2f ms | Error: %.4f%%\\n\\n",
                saturation.metrics.getThroughputPerSecond(),
                saturation.metrics.getAverageResponseTime(),
                saturation.metrics.getErrorRate());
        }
        
        if (breaking != null) {
            System.out.printf("❌ BREAKING POINT: %,d users\\n", breaking.userLevel);
            System.out.printf("   Throughput: %.2f req/s | Response: %.2f ms | Error: %.4f%%\\n\\n",
                breaking.metrics.getThroughputPerSecond(),
                breaking.metrics.getAverageResponseTime(),
                breaking.metrics.getErrorRate());
        }
    }
    
    private void printDetailedMetrics() {
        System.out.println("📊 DETAILED PERFORMANCE METRICS:");
        System.out.println("═══════════════════════════════════════════════════════════════════════");
        System.out.printf("%-8s %-12s %-12s %-12s %-12s %-12s\\n",
            "Users", "Requests", "Throughput", "Avg Resp", "Error Rate", "Status");
        System.out.println("───────────────────────────────────────────────────────────────────────");
        
        for (ScalabilityResult result : results) {
            String status = getPerformanceStatus(result);
            System.out.printf("%-8s %-12s %-12s %-12s %-12s %-12s\\n",
                String.format("%,d", result.userLevel),
                String.format("%,d", result.metrics.getTotalRequests()),
                String.format("%.2f", result.metrics.getThroughputPerSecond()),
                String.format("%.0f ms", result.metrics.getAverageResponseTime()),
                String.format("%.4f%%", result.metrics.getErrorRate()),
                status);
        }
        System.out.println();
    }
    
    private String getPerformanceStatus(ScalabilityResult result) {
        if (result.metrics.getErrorRate() > MAX_ERROR_RATE) return "❌ HIGH ERROR";
        if (result.metrics.getAverageResponseTime() > MAX_RESPONSE_TIME) return "⚠️ SLOW";
        if (result.metrics.getErrorRate() < 1.0 && result.metrics.getAverageResponseTime() < 2000) return "✅ STABLE";
        return "⚠️ DEGRADED";
    }
    
    private void printRecommendations(ScalabilityTestReport report) {
        System.out.println("💡 RECOMMENDATIONS:");
        System.out.println("═══════════════════════════════════════");
        
        if (report.stableLoad != null) {
            System.out.println("✅ Production Capacity: " + (report.stableLoad.userLevel * 0.7) + " concurrent users (70% of stable load)");
        }
        
        if (report.saturationPoint != null) {
            System.out.println("⚠️ Scale Planning: Consider scaling before " + report.saturationPoint.userLevel + " users");
        }
        
        System.out.println("🔧 Monitoring: Set alerts at 80% of stable load capacity");
        System.out.println("📈 Auto-scaling: Configure triggers based on response time > 1s or error rate > 2%");
        System.out.println("🎯 SLA Targets: Response time < 1s, Error rate < 1%, 99.9% availability");
        System.out.println();
    }
    
    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    // Data classes
    public static class ScalabilityResult {
        public final int userLevel;
        public final PerformanceMetrics metrics;
        
        public ScalabilityResult(int userLevel, PerformanceMetrics metrics) {
            this.userLevel = userLevel;
            this.metrics = metrics;
        }
    }
    
    public static class ScalabilityTestReport {
        public LocalDateTime startTime;
        public LocalDateTime endTime;
        public String endpoint;
        public List<ScalabilityResult> results = new ArrayList<>();
        public ScalabilityResult stableLoad;
        public ScalabilityResult saturationPoint;
        public ScalabilityResult breakingPoint;
    }
}