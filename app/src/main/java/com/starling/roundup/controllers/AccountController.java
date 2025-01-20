package com.starling.roundup.controllers;

import com.starling.roundup.components.Account;
import com.starling.roundup.services.AccountsService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for handling account-related API requests.
 */
@RestController
@RequestMapping("/api/accounts")
public class AccountController {
  @Value("${access.token}")
  private String accessToken;

  private final AccountsService accountsService;

  /**
   * Constructor for AccountController.
   *
   * @param accountsService Service to manage account-related operations.
   */
  public AccountController(AccountsService accountsService) {
    this.accountsService = accountsService;
  }

  /**
   * Endpoint to retrieve all accounts.
   *
   * @return ResponseEntity containing account details and status information.
   */
  @GetMapping
  public ResponseEntity<Map<String, Object>> getAllAccounts() {
    try {
      // Retrieve all accounts using the access token.
      List<Account> accounts = accountsService.getAllAccounts(accessToken);

      // Create response payload.
      Map<String, Object> response = new HashMap<>();
      response.put("status", HttpStatus.OK.value());
      response.put("message", "Accounts retrieved successfully.");
      response.put("data", accounts);
      response.put("timestamp", System.currentTimeMillis());

      // Return response.
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      // Handle and propagate exceptions.
      throw new RuntimeException("Error: " + e.getMessage(), e);
    }
  }
}
