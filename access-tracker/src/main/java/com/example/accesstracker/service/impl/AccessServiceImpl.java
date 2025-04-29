package com.example.accesstracker.service.impl;

import com.example.accesstracker.dao.HistoryAccessDao;
import com.example.accesstracker.dao.LastAccessDao;
import com.example.accesstracker.entity.HistoryAccess;
import com.example.accesstracker.entity.LastAccess;
import com.example.accesstracker.service.AccessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Implementation of the AccessService interface
 */
@Service
public class AccessServiceImpl implements AccessService {

    private final LastAccessDao lastAccessDao;
    private final HistoryAccessDao historyAccessDao;

    @Autowired
    public AccessServiceImpl(LastAccessDao lastAccessDao, HistoryAccessDao historyAccessDao) {
        this.lastAccessDao = lastAccessDao;
        this.historyAccessDao = historyAccessDao;
    }

    @Override
    @Async
    @Transactional
    public boolean recordAccess(String logUser, String uuid, String projectId, String versionpbi) {
        try {
            LocalDate today = LocalDate.now();
            LocalDateTime currentDateTime = LocalDateTime.now();
            
            // Check and update LastAccess table
            LastAccess lastAccess = lastAccessDao.getByUserAndProject(logUser, projectId, versionpbi);
            
            if (lastAccess == null) {
                // Create new LastAccess record
                lastAccess = new LastAccess();
                lastAccess.setLogUser(logUser);
                lastAccess.setUuid(uuid);
                lastAccess.setProjectId(projectId);
                lastAccess.setVersionpbi(versionpbi);
                lastAccess.setAccessDate(today);
                lastAccess.setFirstAccessTime(currentDateTime);
                lastAccess.setLastAccessTime(currentDateTime);
                lastAccessDao.insert(lastAccess);
            } else {
                // Update existing LastAccess record - only update the last access time
                lastAccess.setUuid(uuid);
                lastAccess.setAccessDate(today);
                lastAccess.setLastAccessTime(currentDateTime);
                lastAccessDao.update(lastAccess);
            }
            
            // Check and update HistoryAccess table (one record per user per day per project+version)
            boolean hasHistoryForToday = historyAccessDao.hasHistoryForDate(logUser, projectId, versionpbi, today);
            
            if (!hasHistoryForToday) {
                // Create new history record for today
                HistoryAccess historyAccess = new HistoryAccess();
                historyAccess.setLogUser(logUser);
                historyAccess.setUuid(uuid);
                historyAccess.setProjectId(projectId);
                historyAccess.setVersionpbi(versionpbi);
                historyAccess.setAccessDate(today);
                historyAccess.setFirstAccessTime(currentDateTime);
                historyAccess.setLastAccessTime(currentDateTime);
                historyAccessDao.insert(historyAccess);
            } else {
                // Update the last access time for today's record
                HistoryAccess historyAccess = historyAccessDao.getByUserProjectAndDate(logUser, projectId, versionpbi, today);
                historyAccess.setLastAccessTime(currentDateTime);
                historyAccessDao.updateLastAccessTime(historyAccess);
            }
            
            return true;
        } catch (Exception e) {
            // Log the exception
            e.printStackTrace();
            return false;
        }
    }

    @Override
    @Async
    @Transactional
    public boolean updateEndTime(String logUser, String uuid, String projectId, String versionpbi) {
        try {
            LocalDateTime currentDateTime = LocalDateTime.now();
            
            // Update LastAccess end time
            LastAccess lastAccess = lastAccessDao.getByUserAndProject(logUser, projectId, versionpbi);
            if (lastAccess != null && lastAccess.getUuid().equals(uuid)) {
                lastAccess.setLastAccessTime(currentDateTime);
                lastAccessDao.update(lastAccess);
                
                // Also update HistoryAccess end time for today
                LocalDate today = LocalDate.now();
                HistoryAccess historyAccess = historyAccessDao.getByUserProjectAndDate(logUser, projectId, versionpbi, today);
                if (historyAccess != null) {
                    historyAccess.setLastAccessTime(currentDateTime);
                    historyAccessDao.updateLastAccessTime(historyAccess);
                }
                
                return true;
            }
            
            return false;
        } catch (Exception e) {
            // Log the exception
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public LastAccess getLastAccess(String logUser) {
        return lastAccessDao.getByUser(logUser);
    }
    
    @Override
    public LastAccess getLastAccessByUserAndProject(String logUser, String projectId, String versionpbi) {
        return lastAccessDao.getByUserAndProject(logUser, projectId, versionpbi);
    }

    @Override
    public List<HistoryAccess> getUserHistory(String logUser) {
        return historyAccessDao.getAllByUser(logUser);
    }
    
    @Override
    public List<HistoryAccess> getProjectHistory(String projectId) {
        return historyAccessDao.getAllByProject(projectId);
    }

    @Override
    public List<HistoryAccess> getHistoryByDateRange(LocalDate startDate, LocalDate endDate) {
        return historyAccessDao.getByDateRange(startDate, endDate);
    }
}
