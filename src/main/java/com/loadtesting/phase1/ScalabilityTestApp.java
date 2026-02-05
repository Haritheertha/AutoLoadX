package com.loadtesting.phase1;

import com.loadtesting.phase1.service.ScalabilityTester;
import java.util.Scanner;

/**
 * AutoLoadX Scalability Testing Application
 * Comprehensive load testing strategy implementation
 */
public class ScalabilityTestApp {
    
    public static void main(String[] args) {
        System.out.println("🚀 === AutoLoadX Scalability Testing Strategy ===");
        System.out.println("Comprehensive load testing with performance analysis");
        System.out.println();
        
        Scanner scanner = new Scanner(System.in);
        
        // Get test configuration
        System.out.print("Enter target endpoint: ");
        String endpoint = scanner.nextLine().trim();
        
        System.out.print("HTTP Method [GET]: ");
        String method = scanner.nextLine().trim();
        if (method.isEmpty()) method = "GET";
        
        System.out.print("Request body (optional): ");
        String body = scanner.nextLine().trim();
        
        System.out.println();
        System.out.println("📋 TEST CONFIGURATION:");
        System.out.println("Endpoint: " + endpoint);
        System.out.println("Method: " + method);
        System.out.println("User Levels: 10 → 100 → 500 → 1K → 5K → 10K");
        System.out.println("Duration: 5 minutes per level");
        System.out.println("Stabilization: 1 minute between levels");
        System.out.println();
        
        System.out.print("Proceed with scalability test? (y/n): ");
        if (!scanner.nextLine().toLowerCase().startsWith("y")) {
            System.out.println("Test cancelled.");
            scanner.close();
            return;
        }
        
        // Execute scalability test
        ScalabilityTester tester = new ScalabilityTester();
        ScalabilityTester.ScalabilityTestReport report = tester.executeScalabilityTest(endpoint, method, body);
        
        // Print final summary
        printFinalSummary(report);
        
        scanner.close();
    }
    
    private static void printFinalSummary(ScalabilityTester.ScalabilityTestReport report) {
        System.out.println("🎯 === SCALABILITY TEST SUMMARY ===");
        System.out.println("Test Duration: " + java.time.Duration.between(report.startTime, report.endTime).toMinutes() + " minutes");
        System.out.println("Levels Tested: " + report.results.size());
        
        if (report.stableLoad != null) {
            System.out.println("✅ Recommended Production Load: " + (int)(report.stableLoad.userLevel * 0.7) + " users");
        }
        
        if (report.breakingPoint != null) {
            System.out.println("❌ System Breaking Point: " + report.breakingPoint.userLevel + " users");
        }
        
        System.out.println();
        System.out.println("📊 Use these results for:");
        System.out.println("• Production capacity planning");
        System.out.println("• Auto-scaling configuration");
        System.out.println("• SLA definition");
        System.out.println("• Performance monitoring setup");
    }
}