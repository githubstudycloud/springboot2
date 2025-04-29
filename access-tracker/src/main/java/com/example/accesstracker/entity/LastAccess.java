package com.example.accesstracker.entity;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Entity for the last access table
 * Records the last visit information for each user
 */
@Data
public class LastAccess {
    private Long id;
    private String logUser; // User ID or username
    private String uuid; // Unique identifier for the visit session
    private LocalDate accessDate; // Visit date (year, month, day)
    private LocalTime startTime; // Start time of the visit
    private LocalTime endTime; // End time of the visit
}
