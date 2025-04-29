package com.example.accesstracker.service;

import com.example.accesstracker.dao.HistoryAccessDao;
import com.example.accesstracker.dao.LastAccessDao;
import com.example.accesstracker.entity.LastAccess;
import com.example.accesstracker.service.impl.AccessServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class AccessServiceTest {

    @Mock
    private LastAccessDao lastAccessDao;

    @Mock
    private HistoryAccessDao historyAccessDao;

    @InjectMocks
    private AccessServiceImpl accessService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testRecordAccessNewUser() {
        // Arrange
        String logUser = "testUser";
        String uuid = "123e4567-e89b-12d3-a456-426614174000";
        
        when(lastAccessDao.getByUser(logUser)).thenReturn(null);
        when(lastAccessDao.insert(any(LastAccess.class))).thenReturn(1);
        when(historyAccessDao.hasHistoryForDate(eq(logUser), any(LocalDate.class))).thenReturn(false);
        when(historyAccessDao.insert(any())).thenReturn(1);
        
        // Act
        boolean result = accessService.recordAccess(logUser, uuid);
        
        // Assert
        assertTrue(result);
        verify(lastAccessDao, times(1)).insert(any(LastAccess.class));
        verify(historyAccessDao, times(1)).insert(any());
    }

    @Test
    public void testRecordAccessExistingUser() {
        // Arrange
        String logUser = "testUser";
        String uuid = "123e4567-e89b-12d3-a456-426614174000";
        
        LastAccess lastAccess = new LastAccess();
        lastAccess.setId(1L);
        lastAccess.setLogUser(logUser);
        lastAccess.setUuid("old-uuid");
        lastAccess.setAccessDate(LocalDate.now().minusDays(1));
        lastAccess.setStartTime(LocalTime.of(10, 0));
        lastAccess.setEndTime(LocalTime.of(11, 0));
        
        when(lastAccessDao.getByUser(logUser)).thenReturn(lastAccess);
        when(lastAccessDao.update(any(LastAccess.class))).thenReturn(1);
        when(historyAccessDao.hasHistoryForDate(eq(logUser), any(LocalDate.class))).thenReturn(true);
        
        // Act
        boolean result = accessService.recordAccess(logUser, uuid);
        
        // Assert
        assertTrue(result);
        verify(lastAccessDao, times(1)).update(any(LastAccess.class));
        verify(historyAccessDao, never()).insert(any());
    }

    @Test
    public void testUpdateEndTime() {
        // Arrange
        String logUser = "testUser";
        String uuid = "123e4567-e89b-12d3-a456-426614174000";
        
        LastAccess lastAccess = new LastAccess();
        lastAccess.setId(1L);
        lastAccess.setLogUser(logUser);
        lastAccess.setUuid(uuid);
        lastAccess.setAccessDate(LocalDate.now());
        lastAccess.setStartTime(LocalTime.of(10, 0));
        lastAccess.setEndTime(LocalTime.of(10, 0));
        
        when(lastAccessDao.getByUser(logUser)).thenReturn(lastAccess);
        when(lastAccessDao.update(any(LastAccess.class))).thenReturn(1);
        
        // Act
        boolean result = accessService.updateEndTime(logUser, uuid);
        
        // Assert
        assertTrue(result);
        verify(lastAccessDao, times(1)).update(any(LastAccess.class));
    }

    @Test
    public void testGetLastAccess() {
        // Arrange
        String logUser = "testUser";
        
        LastAccess lastAccess = new LastAccess();
        lastAccess.setId(1L);
        lastAccess.setLogUser(logUser);
        lastAccess.setUuid("test-uuid");
        lastAccess.setAccessDate(LocalDate.now());
        lastAccess.setStartTime(LocalTime.of(10, 0));
        lastAccess.setEndTime(LocalTime.of(11, 0));
        
        when(lastAccessDao.getByUser(logUser)).thenReturn(lastAccess);
        
        // Act
        LastAccess result = accessService.getLastAccess(logUser);
        
        // Assert
        assertEquals(lastAccess.getId(), result.getId());
        assertEquals(lastAccess.getLogUser(), result.getLogUser());
        assertEquals(lastAccess.getUuid(), result.getUuid());
        assertEquals(lastAccess.getAccessDate(), result.getAccessDate());
        assertEquals(lastAccess.getStartTime(), result.getStartTime());
        assertEquals(lastAccess.getEndTime(), result.getEndTime());
    }
}
