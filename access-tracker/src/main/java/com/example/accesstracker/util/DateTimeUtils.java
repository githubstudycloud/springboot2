package com.example.accesstracker.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for date and time operations
 */
public class DateTimeUtils {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    private DateTimeUtils() {
        // Private constructor to prevent instantiation
    }
    
    /**
     * Parse a date string in the format yyyy-MM-dd
     * @param dateStr the date string
     * @return the LocalDate object
     */
    public static LocalDate parseDate(String dateStr) {
        return LocalDate.parse(dateStr, DATE_FORMATTER);
    }
    
    /**
     * Parse a time string in the format HH:mm:ss
     * @param timeStr the time string
     * @return the LocalTime object
     */
    public static LocalTime parseTime(String timeStr) {
        return LocalTime.parse(timeStr, TIME_FORMATTER);
    }
    
    /**
     * Parse a datetime string in the format yyyy-MM-dd HH:mm:ss
     * @param dateTimeStr the datetime string
     * @return the LocalDateTime object
     */
    public static LocalDateTime parseDateTime(String dateTimeStr) {
        return LocalDateTime.parse(dateTimeStr, DATETIME_FORMATTER);
    }
    
    /**
     * Format a LocalDate to string in the format yyyy-MM-dd
     * @param date the LocalDate object
     * @return the formatted date string
     */
    public static String formatDate(LocalDate date) {
        return date.format(DATE_FORMATTER);
    }
    
    /**
     * Format a LocalTime to string in the format HH:mm:ss
     * @param time the LocalTime object
     * @return the formatted time string
     */
    public static String formatTime(LocalTime time) {
        return time.format(TIME_FORMATTER);
    }
    
    /**
     * Format a LocalDateTime to string in the format yyyy-MM-dd HH:mm:ss
     * @param dateTime the LocalDateTime object
     * @return the formatted datetime string
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        return dateTime.format(DATETIME_FORMATTER);
    }
    
    /**
     * Get the current date
     * @return the current date
     */
    public static LocalDate getCurrentDate() {
        return LocalDate.now();
    }
    
    /**
     * Get the current time
     * @return the current time
     */
    public static LocalTime getCurrentTime() {
        return LocalTime.now();
    }
    
    /**
     * Get the current datetime
     * @return the current datetime
     */
    public static LocalDateTime getCurrentDateTime() {
        return LocalDateTime.now();
    }
}
