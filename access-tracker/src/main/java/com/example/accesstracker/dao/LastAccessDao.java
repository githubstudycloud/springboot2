package com.example.accesstracker.dao;

import com.example.accesstracker.entity.LastAccess;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface LastAccessDao {
    
    /**
     * Insert a new last access record
     * @param lastAccess the last access record to insert
     * @return the number of rows affected
     */
    int insert(LastAccess lastAccess);
    
    /**
     * Update an existing last access record
     * @param lastAccess the last access record to update
     * @return the number of rows affected
     */
    int update(LastAccess lastAccess);
    
    /**
     * Get last access record by user ID
     * @param logUser the user ID
     * @return the last access record
     */
    LastAccess getByUser(String logUser);
    
    /**
     * Get last access record by user ID, project ID, and version
     * @param logUser the user ID
     * @param projectId the project ID
     * @param versionpbi the version PBI
     * @return the last access record
     */
    LastAccess getByUserAndProject(@Param("logUser") String logUser, 
                                   @Param("projectId") String projectId,
                                   @Param("versionpbi") String versionpbi);
    
    /**
     * Check if a user has accessed today
     * @param logUser the user ID
     * @param accessDate the access date
     * @return true if the user has accessed today, false otherwise
     */
    boolean hasAccessedToday(@Param("logUser") String logUser, @Param("accessDate") LocalDate accessDate);
    
    /**
     * Delete a last access record by user ID
     * @param logUser the user ID
     * @return the number of rows affected
     */
    int deleteByUser(String logUser);
    
    /**
     * Get all last access records
     * @return all last access records
     */
    List<LastAccess> getAll();
    
    /**
     * Get all last access records for a project
     * @param projectId the project ID
     * @return all last access records for the project
     */
    List<LastAccess> getAllByProject(String projectId);
}
