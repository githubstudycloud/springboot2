package com.example.accesstracker.controller;

import com.example.accesstracker.entity.HistoryAccess;
import com.example.accesstracker.entity.LastAccess;
import com.example.accesstracker.service.AccessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Controller for access tracking endpoints
 */
@RestController
@RequestMapping("/api/access")
public class AccessController {

    private final AccessService accessService;

    @Autowired
    public AccessController(AccessService accessService) {
        this.accessService = accessService;
    }

    /**
     * Record a user's access
     * @param logUser the user identifier
     * @return success/failure response
     */
    @PostMapping("/record")
    public ResponseEntity<Map<String, Object>> recordAccess(@RequestParam("logUser") String logUser) {
        Map<String, Object> response = new HashMap<>();
        
        // Generate a UUID for this session
        String uuid = UUID.randomUUID().toString();
        
        boolean result = accessService.recordAccess(logUser, uuid);
        
        response.put("success", result);
        response.put("uuid", uuid);
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Update the end time of a user's session
     * @param logUser the user identifier
     * @param uuid the session UUID
     * @return success/failure response
     */
    @PutMapping("/end")
    public ResponseEntity<Map<String, Object>> endSession(
            @RequestParam("logUser") String logUser,
            @RequestParam("uuid") String uuid) {
        Map<String, Object> response = new HashMap<>();
        
        boolean result = accessService.updateEndTime(logUser, uuid);
        
        response.put("success", result);
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get a user's last access record
     * @param logUser the user identifier
     * @return the last access record
     */
    @GetMapping("/last/{logUser}")
    public ResponseEntity<LastAccess> getLastAccess(@PathVariable("logUser") String logUser) {
        LastAccess lastAccess = accessService.getLastAccess(logUser);
        
        if (lastAccess == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(lastAccess);
    }

    /**
     * Get a user's access history
     * @param logUser the user identifier
     * @return list of history access records
     */
    @GetMapping("/history/user/{logUser}")
    public ResponseEntity<List<HistoryAccess>> getUserHistory(@PathVariable("logUser") String logUser) {
        List<HistoryAccess> historyList = accessService.getUserHistory(logUser);
        
        return ResponseEntity.ok(historyList);
    }

    /**
     * Get access history for a date range
     * @param startDate the start date
     * @param endDate the end date
     * @return list of history access records
     */
    @GetMapping("/history/date-range")
    public ResponseEntity<List<HistoryAccess>> getHistoryByDateRange(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<HistoryAccess> historyList = accessService.getHistoryByDateRange(startDate, endDate);
        
        return ResponseEntity.ok(historyList);
    }
}
