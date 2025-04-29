package com.example.accesstracker.dao;

import com.example.accesstracker.entity.HistoryAccess;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface HistoryAccessDao {
    
    /**
     * Insert a new history access record
     * @param historyAccess the history access record to insert
     * @return the number of rows affected
     */
    int insert(HistoryAccess historyAccess);
    
    /**
     * Update an existing history access record's last access time
     * @param historyAccess the history access record to update
     * @return the number of rows affected
     */
    int updateLastAccessTime(HistoryAccess historyAccess);
    
    /**
     * Get history access record by user ID and date
     * @param logUser the user ID
     * @param accessDate the access date
     * @return the history access record
     */
    HistoryAccess getByUserAndDate(@Param("logUser") String logUser, @Param("accessDate") LocalDate accessDate);
    
    /**
     * Get history access record by user ID, project ID, version, and date
     * @param logUser the user ID
     * @param projectId the project ID
     * @param versionpbi the version PBI
     * @param accessDate the access date
     * @return the history access record
     */
    HistoryAccess getByUserProjectAndDate(@Param("logUser") String logUser, 
                                         @Param("projectId") String projectId,
                                         @Param("versionpbi") String versionpbi,
                                         @Param("accessDate") LocalDate accessDate);
    
    /**
     * Check if a user has a history record for a specific date
     * @param logUser the user ID
     * @param projectId the project ID
     * @param versionpbi the version PBI
     * @param accessDate the access date
     * @return true if the user has a history record for the date, false otherwise
     */
    boolean hasHistoryForDate(@Param("logUser") String logUser, 
                             @Param("projectId") String projectId,
                             @Param("versionpbi") String versionpbi,
                             @Param("accessDate") LocalDate accessDate);
    
    /**
     * Get all history access records for a user
     * @param logUser the user ID
     * @return all history access records for the user
     */
    List<HistoryAccess> getAllByUser(String logUser);
    
    /**
     * Get all history access records for a project
     * @param projectId the project ID
     * @return all history access records for the project
     */
    List<HistoryAccess> getAllByProject(String projectId);
    
    /**
     * Get all history access records for a date range
     * @param startDate the start date
     * @param endDate the end date
     * @return all history access records for the date range
     */
    List<HistoryAccess> getByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
