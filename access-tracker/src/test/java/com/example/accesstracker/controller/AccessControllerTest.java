package com.example.accesstracker.controller;

import com.example.accesstracker.entity.LastAccess;
import com.example.accesstracker.service.AccessService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccessController.class)
public class AccessControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccessService accessService;

    @Test
    public void testRecordAccess() throws Exception {
        // Arrange
        when(accessService.recordAccess(anyString(), anyString(), anyString(), anyString())).thenReturn(true);

        // Act & Assert
        mockMvc.perform(post("/api/access/record")
                .param("logUser", "testUser")
                .param("projectId", "project1")
                .param("versionpbi", "v1.0")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.uuid").exists())
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    public void testGetLastAccess() throws Exception {
        // Arrange
        LastAccess lastAccess = new LastAccess();
        lastAccess.setId(1L);
        lastAccess.setLogUser("testUser");
        lastAccess.setUuid("123e4567-e89b-12d3-a456-426614174000");
        lastAccess.setProjectId("project1");
        lastAccess.setVersionpbi("v1.0");
        lastAccess.setAccessDate(LocalDate.now());
        lastAccess.setFirstAccessTime(LocalDateTime.now().minusHours(1));
        lastAccess.setLastAccessTime(LocalDateTime.now());

        when(accessService.getLastAccess("testUser")).thenReturn(lastAccess);

        // Act & Assert
        mockMvc.perform(get("/api/access/last/testUser")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.logUser").value("testUser"))
                .andExpect(jsonPath("$.uuid").value("123e4567-e89b-12d3-a456-426614174000"))
                .andExpect(jsonPath("$.projectId").value("project1"))
                .andExpect(jsonPath("$.versionpbi").value("v1.0"))
                .andExpect(jsonPath("$.accessDate").exists())
                .andExpect(jsonPath("$.firstAccessTime").exists())
                .andExpect(jsonPath("$.lastAccessTime").exists());
    }

    @Test
    public void testGetLastAccessByProject() throws Exception {
        // Arrange
        LastAccess lastAccess = new LastAccess();
        lastAccess.setId(1L);
        lastAccess.setLogUser("testUser");
        lastAccess.setUuid("123e4567-e89b-12d3-a456-426614174000");
        lastAccess.setProjectId("project1");
        lastAccess.setVersionpbi("v1.0");
        lastAccess.setAccessDate(LocalDate.now());
        lastAccess.setFirstAccessTime(LocalDateTime.now().minusHours(1));
        lastAccess.setLastAccessTime(LocalDateTime.now());

        when(accessService.getLastAccessByUserAndProject("testUser", "project1", "v1.0")).thenReturn(lastAccess);

        // Act & Assert
        mockMvc.perform(get("/api/access/last/project")
                .param("logUser", "testUser")
                .param("projectId", "project1")
                .param("versionpbi", "v1.0")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.logUser").value("testUser"))
                .andExpect(jsonPath("$.projectId").value("project1"))
                .andExpect(jsonPath("$.versionpbi").value("v1.0"));
    }

    @Test
    public void testGetLastAccessNotFound() throws Exception {
        // Arrange
        when(accessService.getLastAccess("nonExistentUser")).thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/api/access/last/nonExistentUser")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
