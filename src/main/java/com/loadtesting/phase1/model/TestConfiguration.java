package com.loadtesting.phase1.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Phase 1: Test Configuration
 * Holds minimal inputs required for load testing
 */
public class TestConfiguration {
    private String apiEndpoint;
    private String httpMethod;
    private Map<String, String> headers;
    private String requestBody;
    private int testDurationSeconds;
    private int concurrentUsers;
    private int rampUpSeconds;
    
    public TestConfiguration() {
        this.httpMethod = "GET";
        this.headers = new HashMap<>();
        this.testDurationSeconds = 60;
        this.concurrentUsers = 10;
        this.rampUpSeconds = 10;
    }
    
    // Getters and Setters
    public String getApiEndpoint() { return apiEndpoint; }
    public void setApiEndpoint(String apiEndpoint) { this.apiEndpoint = apiEndpoint; }
    
    public String getHttpMethod() { return httpMethod; }
    public void setHttpMethod(String httpMethod) { this.httpMethod = httpMethod; }
    
    public Map<String, String> getHeaders() { return headers; }
    public void setHeaders(Map<String, String> headers) { this.headers = headers; }
    
    public String getRequestBody() { return requestBody; }
    public void setRequestBody(String requestBody) { this.requestBody = requestBody; }
    
    public int getTestDurationSeconds() { return testDurationSeconds; }
    public void setTestDurationSeconds(int testDurationSeconds) { this.testDurationSeconds = testDurationSeconds; }
    
    public int getConcurrentUsers() { return concurrentUsers; }
    public void setConcurrentUsers(int concurrentUsers) { this.concurrentUsers = concurrentUsers; }
    
    public int getRampUpSeconds() { return rampUpSeconds; }
    public void setRampUpSeconds(int rampUpSeconds) { this.rampUpSeconds = rampUpSeconds; }
    
    public boolean isValid() {
        return apiEndpoint != null && !apiEndpoint.trim().isEmpty();
    }
}