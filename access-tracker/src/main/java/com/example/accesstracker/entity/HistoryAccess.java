package com.example.accesstracker.entity;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity for the history access table
 * Records only one visit per user per day
 */
@Data
public class HistoryAccess {
    private Long id;
    private String logUser; // User ID or username
    private String uuid; // Unique identifier for the visit session
    private String projectId; // Project identifier
    private String versionpbi; // Version PBI identifier
    private LocalDate accessDate; // Visit date (year, month, day)
    private LocalDateTime firstAccessTime; // First access datetime of the day
    private LocalDateTime lastAccessTime; // Last access datetime of the day
}
