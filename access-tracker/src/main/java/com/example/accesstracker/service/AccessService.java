package com.example.accesstracker.service;

import com.example.accesstracker.entity.HistoryAccess;
import com.example.accesstracker.entity.LastAccess;

import java.time.LocalDate;
import java.util.List;

/**
 * Service interface for access tracking
 */
public interface AccessService {
    
    /**
     * Record a user access
     * @param logUser the user ID
     * @param uuid unique identifier for the session
     * @return true if the recording was successful, false otherwise
     */
    boolean recordAccess(String logUser, String uuid);
    
    /**
     * Update the end time of a session
     * @param logUser the user ID
     * @param uuid unique identifier for the session
     * @return true if the update was successful, false otherwise
     */
    boolean updateEndTime(String logUser, String uuid);
    
    /**
     * Get last access record for a user
     * @param logUser the user ID
     * @return the last access record
     */
    LastAccess getLastAccess(String logUser);
    
    /**
     * Get history access records for a user
     * @param logUser the user ID
     * @return list of history access records
     */
    List<HistoryAccess> getUserHistory(String logUser);
    
    /**
     * Get history access records for a date range
     * @param startDate the start date
     * @param endDate the end date
     * @return list of history access records
     */
    List<HistoryAccess> getHistoryByDateRange(LocalDate startDate, LocalDate endDate);
}
