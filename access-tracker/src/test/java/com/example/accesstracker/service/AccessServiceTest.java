package com.example.accesstracker.service;

import com.example.accesstracker.dao.HistoryAccessDao;
import com.example.accesstracker.dao.LastAccessDao;
import com.example.accesstracker.entity.HistoryAccess;
import com.example.accesstracker.entity.LastAccess;
import com.example.accesstracker.service.impl.AccessServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
        String projectId = "project1";
        String versionpbi = "v1.0";
        
        when(lastAccessDao.getByUserAndProject(logUser, projectId, versionpbi)).thenReturn(null);
        when(lastAccessDao.insert(any(LastAccess.class))).thenReturn(1);
        when(historyAccessDao.hasHistoryForDate(eq(logUser), eq(projectId), eq(versionpbi), any(LocalDate.class))).thenReturn(false);
        when(historyAccessDao.insert(any(HistoryAccess.class))).thenReturn(1);
        
        // Act
        boolean result = accessService.recordAccess(logUser, uuid, projectId, versionpbi);
        
        // Assert
        assertTrue(result);
        verify(lastAccessDao, times(1)).insert(any(LastAccess.class));
        verify(historyAccessDao, times(1)).insert(any(HistoryAccess.class));
    }

    @Test
    public void testRecordAccessExistingUser() {
        // Arrange
        String logUser = "testUser";
        String uuid = "123e4567-e89b-12d3-a456-426614174000";
        String projectId = "project1";
        String versionpbi = "v1.0";
        
        LastAccess lastAccess = new LastAccess();
        lastAccess.setId(1L);
        lastAccess.setLogUser(logUser);
        lastAccess.setUuid("old-uuid");
        lastAccess.setProjectId(projectId);
        lastAccess.setVersionpbi(versionpbi);
        lastAccess.setAccessDate(LocalDate.now().minusDays(1));
        lastAccess.setFirstAccessTime(LocalDateTime.now().minusDays(1));
        lastAccess.setLastAccessTime(LocalDateTime.now().minusDays(1));
        
        when(lastAccessDao.getByUserAndProject(logUser, projectId, versionpbi)).thenReturn(lastAccess);
        when(lastAccessDao.update(any(LastAccess.class))).thenReturn(1);
        when(historyAccessDao.hasHistoryForDate(eq(logUser), eq(projectId), eq(versionpbi), any(LocalDate.class))).thenReturn(true);
        
        HistoryAccess historyAccess = new HistoryAccess();
        historyAccess.setId(1L);
        historyAccess.setLogUser(logUser);
        historyAccess.setUuid("old-uuid");
        historyAccess.setProjectId(projectId);
        historyAccess.setVersionpbi(versionpbi);
        historyAccess.setAccessDate(LocalDate.now());
        historyAccess.setFirstAccessTime(LocalDateTime.now().minusHours(1));
        historyAccess.setLastAccessTime(LocalDateTime.now().minusHours(1));
        
        when(historyAccessDao.getByUserProjectAndDate(eq(logUser), eq(projectId), eq(versionpbi), any(LocalDate.class))).thenReturn(historyAccess);
        when(historyAccessDao.updateLastAccessTime(any(HistoryAccess.class))).thenReturn(1);
        
        // Act
        boolean result = accessService.recordAccess(logUser, uuid, projectId, versionpbi);
        
        // Assert
        assertTrue(result);
        verify(lastAccessDao, times(1)).update(any(LastAccess.class));
        verify(historyAccessDao, times(1)).updateLastAccessTime(any(HistoryAccess.class));
        verify(historyAccessDao, never()).insert(any());
    }

    @Test
    public void testUpdateEndTime() {
        // Arrange
        String logUser = "testUser";
        String uuid = "123e4567-e89b-12d3-a456-426614174000";
        String projectId = "project1";
        String versionpbi = "v1.0";
        
        LastAccess lastAccess = new LastAccess();
        lastAccess.setId(1L);
        lastAccess.setLogUser(logUser);
        lastAccess.setUuid(uuid);
        lastAccess.setProjectId(projectId);
        lastAccess.setVersionpbi(versionpbi);
        lastAccess.setAccessDate(LocalDate.now());
        lastAccess.setFirstAccessTime(LocalDateTime.now().minusHours(1));
        lastAccess.setLastAccessTime(LocalDateTime.now().minusHours(1));
        
        when(lastAccessDao.getByUserAndProject(logUser, projectId, versionpbi)).thenReturn(lastAccess);
        when(lastAccessDao.update(any(LastAccess.class))).thenReturn(1);
        
        HistoryAccess historyAccess = new HistoryAccess();
        historyAccess.setId(1L);
        historyAccess.setLogUser(logUser);
        historyAccess.setUuid(uuid);
        historyAccess.setProjectId(projectId);
        historyAccess.setVersionpbi(versionpbi);
        historyAccess.setAccessDate(LocalDate.now());
        historyAccess.setFirstAccessTime(LocalDateTime.now().minusHours(1));
        historyAccess.setLastAccessTime(LocalDateTime.now().minusHours(1));
        
        when(historyAccessDao.getByUserProjectAndDate(eq(logUser), eq(projectId), eq(versionpbi), any(LocalDate.class))).thenReturn(historyAccess);
        when(historyAccessDao.updateLastAccessTime(any(HistoryAccess.class))).thenReturn(1);
        
        // Act
        boolean result = accessService.updateEndTime(logUser, uuid, projectId, versionpbi);
        
        // Assert
        assertTrue(result);
        verify(lastAccessDao, times(1)).update(any(LastAccess.class));
        verify(historyAccessDao, times(1)).updateLastAccessTime(any(HistoryAccess.class));
    }

    @Test
    public void testGetLastAccessByUserAndProject() {
        // Arrange
        String logUser = "testUser";
        String projectId = "project1";
        String versionpbi = "v1.0";
        
        LastAccess lastAccess = new LastAccess();
        lastAccess.setId(1L);
        lastAccess.setLogUser(logUser);
        lastAccess.setUuid("test-uuid");
        lastAccess.setProjectId(projectId);
        lastAccess.setVersionpbi(versionpbi);
        lastAccess.setAccessDate(LocalDate.now());
        lastAccess.setFirstAccessTime(LocalDateTime.now().minusHours(1));
        lastAccess.setLastAccessTime(LocalDateTime.now());
        
        when(lastAccessDao.getByUserAndProject(logUser, projectId, versionpbi)).thenReturn(lastAccess);
        
        // Act
        LastAccess result = accessService.getLastAccessByUserAndProject(logUser, projectId, versionpbi);
        
        // Assert
        assertEquals(lastAccess.getId(), result.getId());
        assertEquals(lastAccess.getLogUser(), result.getLogUser());
        assertEquals(lastAccess.getUuid(), result.getUuid());
        assertEquals(lastAccess.getProjectId(), result.getProjectId());
        assertEquals(lastAccess.getVersionpbi(), result.getVersionpbi());
        assertEquals(lastAccess.getAccessDate(), result.getAccessDate());
        assertEquals(lastAccess.getFirstAccessTime(), result.getFirstAccessTime());
        assertEquals(lastAccess.getLastAccessTime(), result.getLastAccessTime());
    }
}
