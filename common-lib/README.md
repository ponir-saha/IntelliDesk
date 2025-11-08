# Common Library

Shared DTOs, utilities, and models for IntelliDesk microservices.

## Contents

### DTOs
- `ApiResponse<T>`: Generic API response wrapper
- `PageResponse<T>`: Paginated response wrapper

### Exceptions
- `ResourceNotFoundException`: For missing resources
- `BadRequestException`: For invalid requests
- `UnauthorizedException`: For authentication failures

### Utilities
- `DateTimeUtil`: Date and time formatting utilities
- `ValidationUtil`: Common validation methods

### Constants
- `AppConstants`: Application-wide constants

## Usage

Add this as a dependency in your microservice:

```xml
<dependency>
    <groupId>com.intellidesk</groupId>
    <artifactId>common-lib</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Building

```bash
mvn clean install
```
