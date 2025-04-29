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
import java.time.LocalTime;
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
    public boolean recordAccess(String logUser, String uuid) {
        try {
            LocalDate today = LocalDate.now();
            LocalTime currentTime = LocalTime.now();
            
            // Check and update LastAccess table
            LastAccess lastAccess = lastAccessDao.getByUser(logUser);
            if (lastAccess == null) {
                // Create new LastAccess record
                lastAccess = new LastAccess();
                lastAccess.setLogUser(logUser);
                lastAccess.setUuid(uuid);
                lastAccess.setAccessDate(today);
                lastAccess.setStartTime(currentTime);
                lastAccess.setEndTime(currentTime);
                lastAccessDao.insert(lastAccess);
            } else {
                // Update existing LastAccess record
                lastAccess.setUuid(uuid);
                lastAccess.setAccessDate(today);
                lastAccess.setStartTime(currentTime);
                lastAccess.setEndTime(currentTime);
                lastAccessDao.update(lastAccess);
            }
            
            // Check and update HistoryAccess table (one record per user per day)
            boolean hasHistoryForToday = historyAccessDao.hasHistoryForDate(logUser, today);
            if (!hasHistoryForToday) {
                HistoryAccess historyAccess = new HistoryAccess();
                historyAccess.setLogUser(logUser);
                historyAccess.setUuid(uuid);
                historyAccess.setAccessDate(today);
                historyAccess.setStartTime(currentTime);
                historyAccess.setEndTime(currentTime);
                historyAccessDao.insert(historyAccess);
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
    public boolean updateEndTime(String logUser, String uuid) {
        try {
            LocalTime currentTime = LocalTime.now();
            
            // Update LastAccess end time
            LastAccess lastAccess = lastAccessDao.getByUser(logUser);
            if (lastAccess != null && lastAccess.getUuid().equals(uuid)) {
                lastAccess.setEndTime(currentTime);
                lastAccessDao.update(lastAccess);
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
    public List<HistoryAccess> getUserHistory(String logUser) {
        return historyAccessDao.getAllByUser(logUser);
    }

    @Override
    public List<HistoryAccess> getHistoryByDateRange(LocalDate startDate, LocalDate endDate) {
        return historyAccessDao.getByDateRange(startDate, endDate);
    }
}
