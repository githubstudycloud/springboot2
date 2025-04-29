package com.example.accesstracker.entity;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity for the last access table
 * Records the last visit information for each user
 */
@Data
public class LastAccess {
    private Long id;
    private String logUser; // User ID or username
    private String uuid; // Unique identifier for the visit session
    private String projectId; // Project identifier
    private String versionpbi; // Version PBI identifier
    private LocalDate accessDate; // Visit date (year, month, day)
    private LocalDateTime firstAccessTime; // First access datetime (year, month, day, hour, minute, second)
    private LocalDateTime lastAccessTime; // Last access datetime (year, month, day, hour, minute, second)
}
