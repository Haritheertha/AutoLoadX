package com.loadtesting.phase1.service;

import com.loadtesting.phase1.model.RequestResult;
import com.loadtesting.phase1.model.TestConfiguration;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Phase 1: HTTP Request Executor
 * Handles individual HTTP request execution
 */
public class HttpRequestExecutor {
    private final HttpClient httpClient;
    
    public HttpRequestExecutor() {
        this.httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    }
    
    public RequestResult executeRequest(TestConfiguration config, int threadId) {
        long startTime = System.currentTimeMillis();
        
        try {
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(config.getApiEndpoint()))
                .timeout(Duration.ofSeconds(30));
            
            // Add headers
            config.getHeaders().forEach(requestBuilder::header);
            
            // Set HTTP method and body
            switch (config.getHttpMethod().toUpperCase()) {
                case "POST":
                    requestBuilder.POST(config.getRequestBody() != null ? 
                        HttpRequest.BodyPublishers.ofString(config.getRequestBody()) : 
                        HttpRequest.BodyPublishers.noBody());
                    break;
                case "PUT":
                    requestBuilder.PUT(config.getRequestBody() != null ? 
                        HttpRequest.BodyPublishers.ofString(config.getRequestBody()) : 
                        HttpRequest.BodyPublishers.noBody());
                    break;
                case "DELETE":
                    requestBuilder.DELETE();
                    break;
                default:
                    requestBuilder.GET();
            }
            
            HttpRequest request = requestBuilder.build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            long endTime = System.currentTimeMillis();
            long responseTime = endTime - startTime;
            
            boolean success = response.statusCode() >= 200 && response.statusCode() < 400;
            
            return new RequestResult(responseTime, response.statusCode(), success, null, threadId);
            
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            long responseTime = endTime - startTime;
            
            return new RequestResult(responseTime, 0, false, e.getMessage(), threadId);
        }
    }
}