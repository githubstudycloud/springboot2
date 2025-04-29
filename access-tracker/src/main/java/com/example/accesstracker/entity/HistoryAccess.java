package com.example.accesstracker.entity;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Entity for the history access table
 * Records only one visit per user per day
 */
@Data
public class HistoryAccess {
    private Long id;
    private String logUser; // User ID or username
    private String uuid; // Unique identifier for the visit session
    private LocalDate accessDate; // Visit date (year, month, day)
    private LocalTime startTime; // Start time of the first visit of the day
    private LocalTime endTime; // End time of the first visit of the day
}
