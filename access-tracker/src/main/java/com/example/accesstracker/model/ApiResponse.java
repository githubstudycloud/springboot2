package com.example.accesstracker.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * API response model
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    
    /**
     * Create a success response
     * @param data the response data
     * @param <T> the data type
     * @return a success response
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "Success", data);
    }
    
    /**
     * Create a success response with message
     * @param message the success message
     * @param data the response data
     * @param <T> the data type
     * @return a success response
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }
    
    /**
     * Create an error response
     * @param message the error message
     * @param <T> the data type
     * @return an error response
     */
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null);
    }
}
