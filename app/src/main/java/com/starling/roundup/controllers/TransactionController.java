package com.starling.roundup.controllers;

import com.starling.roundup.components.Transactions;
import com.starling.roundup.services.TransactionService;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Controller for handling transaction-related API requests. */
@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

  private final TransactionService transactionService;

  /**
   * Creates a new instance of TransactionController.
   *
   * @param transactionService Service to manage transaction-related operations.
   */
  public TransactionController(TransactionService transactionService) {
    this.transactionService = transactionService;
  }

  /**
   * Retrieves weekly transactions starting from the provided date.
   *
   * @param startDate Start date in ddMMyyyy format.
   * @return ResponseEntity with status, message, data, and timestamp.
   */
  @GetMapping
  public ResponseEntity<Map<String, Object>> getWeeklyTransactions(
      @RequestParam("startDate") @DateTimeFormat(pattern = "ddMMyyyy") LocalDate startDate) {
    try {
      List<Transactions> transactions = transactionService.getTransactionsBetweenDates(startDate);

      Map<String, Object> response = new HashMap<>();
      response.put("status", HttpStatus.OK.value());
      response.put("message", "Weekly transactions retrieved successfully.");
      response.put("data", transactions);
      response.put("timestamp", System.currentTimeMillis());

      return ResponseEntity.ok(response);
    } catch (Exception e) {
      throw new RuntimeException("Error retrieving weekly transactions: " + e.getMessage(), e);
    }
  }
}
