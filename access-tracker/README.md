# Access Tracker

A Spring Boot 2.7 application for tracking user access patterns with Java 8 and MyBatis.

## Features

- Records the last access for each user
- Maintains a history of user accesses (one record per user per day)
- Asynchronous processing of access records
- RESTful API for access tracking and retrieval

## Database Schema

The application uses two tables:

1. **last_access** - Stores the most recent access for each user
2. **history_access** - Stores one record per user per day

## API Endpoints

### Record User Access
```
POST /api/access/record?logUser={logUser}
```

### End User Session
```
PUT /api/access/end?logUser={logUser}&uuid={uuid}
```

### Get Last Access for User
```
GET /api/access/last/{logUser}
```

### Get Access History for User
```
GET /api/access/history/user/{logUser}
```

### Get Access History for Date Range
```
GET /api/access/history/date-range?startDate={yyyy-MM-dd}&endDate={yyyy-MM-dd}
```

## Technology Stack

- Spring Boot 2.7
- Java 8
- MyBatis
- MySQL

## Running the Application

1. Create the database using the SQL script in `src/main/resources/db/init.sql`
2. Configure the database connection in `application.yml`
3. Run the application using:
   ```
   ./mvnw spring-boot:run
   ```

## Configuration

You can customize the configuration in the `application.yml` file, including:

- Database connection settings
- Thread pool configuration for async processing
- Server port
