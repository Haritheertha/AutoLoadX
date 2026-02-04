# AutoLoadX - Phase 1

## Architecture Overview

### Core Classes:
- `TestConfiguration` - Holds test parameters
- `LoadTestExecutor` - Main execution engine with multithreading
- `MetricsCollector` - Thread-safe metrics collection
- `PerformanceMetrics` - Raw performance data model
- `HttpRequestExecutor` - HTTP request execution
- `SimpleGUI` - Minimal input interface

### Design Principles:
- Clean separation of concerns
- Thread-safe operations
- Modular design for Phase 2 extensions
- No AI/ML logic
- Focus on raw metrics collection

### Phase 1 Scope:
✅ Minimal GUI inputs
✅ Automated load test execution
✅ Multithreaded concurrent users
✅ Raw metrics collection
✅ Structured data exposure

### Phase 2 Ready:
- AI analysis hooks
- PDF reporting integration
- Advanced GUI features