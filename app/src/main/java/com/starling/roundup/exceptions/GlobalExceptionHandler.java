package com.starling.roundup.exceptions;

import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/** Global exception handler for the application. */
@ControllerAdvice
public class GlobalExceptionHandler {

  /**
   * Handles missing request parameter exceptions.
   *
   * @param ex The MissingServletRequestParameterException.
   * @return ResponseEntity with error details.
   */
  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ResponseEntity<Map<String, Object>> handleMissingParams(
      MissingServletRequestParameterException ex) {
    String parameterName = ex.getParameterName();

    Map<String, Object> errorResponse = new HashMap<>();
    errorResponse.put("status", HttpStatus.BAD_REQUEST.value());
    errorResponse.put("error", "Bad Request");
    errorResponse.put("message", "Required request parameter '" + parameterName + "' is missing.");
    errorResponse.put("timestamp", System.currentTimeMillis());

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
  }

  /**
   * Handles all general exceptions.
   *
   * @param ex The Exception.
   * @return ResponseEntity with error details.
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, Object>> handleGeneralException(Exception ex) {
    Map<String, Object> errorResponse = new HashMap<>();
    errorResponse.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
    errorResponse.put("error", "Internal Server Error");
    errorResponse.put("message", ex.getMessage());
    errorResponse.put("timestamp", System.currentTimeMillis());

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
  }
}
