# AutoLoadX

An intelligent performance testing tool inspired by Apache JMeter that reduces manual configuration and automates load testing with intelligent decision-making capabilities.

## Features

- **JMeter-like GUI**: Intuitive JavaFX interface for easy test configuration
- **Intelligent Load Control**: Automatic thread adjustment based on performance metrics
- **SLA-based Testing**: Define response time, error rate, and throughput thresholds
- **Real-time Analysis**: Continuous monitoring and performance evaluation
- **Automated Reporting**: One-click PDF report generation with charts and analysis
- **Pass/Fail Decisions**: Automatic test result determination based on SLA compliance

## Technologies Used

- **Java 11+**: Core application development
- **JavaFX**: Modern GUI framework
- **Apache HttpClient**: HTTP request handling
- **JFreeChart**: Performance charts and graphs
- **iText PDF**: Comprehensive PDF report generation
- **Maven**: Dependency management and build automation

## Prerequisites

- Java 11 or higher
- Maven 3.6+
- JavaFX runtime (included in dependencies)

## Installation & Setup

1. **Clone or download the project**
   ```bash
   cd C:\Users\HP\Load_Testing
   ```

2. **Build the project**
   ```bash
   mvn clean compile
   ```

3. **Run the application**
   ```bash
   mvn javafx:run
   ```

   Or alternatively:
   ```bash
   mvn clean compile exec:java -Dexec.mainClass="com.loadtesting.MainApp"
   ```

## Usage Guide

### 1. Test Configuration
- **API Endpoint**: Enter the URL you want to test (e.g., https://httpbin.org/get)
- **HTTP Method**: Select GET, POST, PUT, or DELETE
- **Request Body**: Add JSON payload for POST/PUT requests (optional)

### 2. SLA Configuration
- **Max Response Time**: Maximum acceptable response time in milliseconds
- **Max Error Rate**: Maximum acceptable error percentage
- **Min Throughput**: Minimum required requests per second

### 3. Load Configuration
- **Initial Threads**: Starting number of concurrent users
- **Max Threads**: Maximum number of concurrent users
- **Test Duration**: Total test duration in seconds

### 4. Running Tests
1. Configure all parameters
2. Click "Start Test"
3. Monitor real-time status updates
4. System automatically adjusts load based on performance
5. Test stops when duration expires or system becomes unstable

### 5. Report Generation
1. Click "Generate Report" after test completion
2. Choose save location for PDF report
3. Report includes:
   - Test configuration summary
   - Performance metrics and charts
   - SLA compliance analysis
   - Pass/fail determination
   - Recommendations

## How It Works

### Intelligent Load Control
The tool automatically:
1. Starts with initial thread count
2. Monitors response time, error rate, and throughput
3. Increases load if performance is within SLA thresholds
4. Stops increasing load when system approaches limits
5. Determines maximum stable load capacity

### SLA Analysis
- **Response Time**: Compares average response time against threshold
- **Error Rate**: Calculates percentage of failed requests
- **Throughput**: Measures requests processed per second
- **Overall Status**: PASS if all SLA criteria are met

### Automated Decision Making
The system uses predefined rules to:
- Determine when to increase load (performance buffer zones)
- Detect system instability
- Stop tests automatically when limits are reached
- Generate pass/fail conclusions

## Example Test Scenarios

### API Performance Test
```
Endpoint: https://api.example.com/users
Method: GET
Max Response Time: 1000ms
Max Error Rate: 2%
Min Throughput: 50 req/s
```

### Load Test with POST Requests
```
Endpoint: https://api.example.com/data
Method: POST
Request Body: {"test": "data"}
Max Response Time: 2000ms
Max Error Rate: 5%
Min Throughput: 20 req/s
```

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/loadtesting/
│   │       ├── MainApp.java              # Application entry point
│   │       ├── controller/
│   │       │   └── MainController.java   # GUI controller
│   │       ├── model/
│   │       │   ├── TestConfiguration.java # Test settings
│   │       │   └── TestResult.java       # Result data model
│   │       ├── service/
│   │       │   ├── HttpClientService.java    # HTTP request handler
│   │       │   ├── LoadTestingEngine.java    # Core testing engine
│   │       │   ├── PerformanceAnalyzer.java  # Metrics analysis
│   │       │   └── ReportGenerator.java      # PDF report creation
│   │       └── util/
│   │           └── TestUtils.java        # Utility functions
│   └── resources/
│       └── main.fxml                     # GUI layout
└── pom.xml                               # Maven configuration
```

## Troubleshooting

### Common Issues

1. **JavaFX Runtime Error**
   - Ensure JavaFX is properly configured
   - Use `mvn javafx:run` instead of direct java execution

2. **Connection Timeouts**
   - Check network connectivity
   - Verify API endpoint accessibility
   - Adjust timeout settings if needed

3. **Memory Issues with High Load**
   - Increase JVM heap size: `-Xmx2g`
   - Reduce maximum thread count
   - Monitor system resources

### Performance Tips

- Start with lower thread counts for initial testing
- Use realistic SLA thresholds based on application requirements
- Monitor system resources during high-load tests
- Test against staging environments when possible

## Contributing

1. Fork the repository
2. Create feature branch
3. Implement changes with tests
4. Submit pull request

## License

This project is open source and available under the MIT License.

## Support

For issues and questions:
1. Check the troubleshooting section
2. Review application logs
3. Create an issue with detailed error information