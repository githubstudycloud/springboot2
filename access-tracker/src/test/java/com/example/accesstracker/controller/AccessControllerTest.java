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
import java.time.LocalTime;

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
        when(accessService.recordAccess(anyString(), anyString())).thenReturn(true);

        // Act & Assert
        mockMvc.perform(post("/api/access/record")
                .param("logUser", "testUser")
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
        lastAccess.setAccessDate(LocalDate.now());
        lastAccess.setStartTime(LocalTime.of(10, 0));
        lastAccess.setEndTime(LocalTime.of(11, 0));

        when(accessService.getLastAccess("testUser")).thenReturn(lastAccess);

        // Act & Assert
        mockMvc.perform(get("/api/access/last/testUser")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.logUser").value("testUser"))
                .andExpect(jsonPath("$.uuid").value("123e4567-e89b-12d3-a456-426614174000"))
                .andExpect(jsonPath("$.accessDate").exists())
                .andExpect(jsonPath("$.startTime").exists())
                .andExpect(jsonPath("$.endTime").exists());
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
