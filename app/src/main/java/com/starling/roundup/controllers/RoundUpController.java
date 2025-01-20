package com.starling.roundup.controllers;

import com.starling.roundup.components.Account;
import com.starling.roundup.components.Transactions;
import com.starling.roundup.services.AccountsService;
import com.starling.roundup.services.RoundUpService;
import com.starling.roundup.services.SavingGoalService;
import com.starling.roundup.services.TransactionService;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Controller for handling round-up related operations. */
@RestController
@RequestMapping("/api/roundup")
public class RoundUpController {

  @Value("${access.token}")
  private String accessToken;

  private final TransactionService transactionService;
  private final RoundUpService roundUpService;
  private final SavingGoalService savingGoalService;
  private final AccountsService accountsService;

  /**
   * Initializes a new instance of the RoundUpController.
   *
   * @param transactionService Service to handle transaction-related operations.
   * @param roundUpService Service to calculate round-up amounts.
   * @param savingGoalService Service to manage savings goals.
   * @param accountsService Service to handle account-related operations.
   */
  public RoundUpController(
      TransactionService transactionService,
      RoundUpService roundUpService,
      SavingGoalService savingGoalService,
      AccountsService accountsService) {
    this.transactionService = transactionService;
    this.roundUpService = roundUpService;
    this.savingGoalService = savingGoalService;
    this.accountsService = accountsService;
  }

  /** Calculates the round-up amount for transactions within a given date range. */
  @GetMapping("/value")
  public ResponseEntity<Map<String, Object>> getRoundUp(
      @RequestParam("startDate") @DateTimeFormat(pattern = "ddMMyyyy") LocalDate startDate) {
    try {
      List<Transactions> transactions = transactionService.getTransactionsBetweenDates(startDate);
      int totalRoundUp = roundUpService.calculateRoundUp(transactions);

      Map<String, Object> response = new HashMap<>();
      response.put("status", HttpStatus.OK.value());
      response.put("message", "Round-up calculated successfully.");
      response.put("roundUpTotalMinorUnits", totalRoundUp);
      response.put("currency", "GBP");
      response.put("timestamp", System.currentTimeMillis());

      return ResponseEntity.ok(response);
    } catch (Exception e) {
      throw new RuntimeException("Error calculating round-up: " + e.getMessage(), e);
    }
  }

  /** Performs the round-up operation and updates the savings goal. */
  @GetMapping("/perform")
  public ResponseEntity<Map<String, Object>> performRoundUp(
      @RequestParam("startDate") @DateTimeFormat(pattern = "ddMMyyyy") LocalDate startDate) {
    try {
      List<Transactions> transactions = transactionService.getTransactionsBetweenDates(startDate);
      int totalRoundUp = roundUpService.calculateRoundUp(transactions);

      if (totalRoundUp <= 0) {
        throw new RuntimeException("No round-up amount to add.");
      }

      List<Account> accounts = accountsService.getAllAccounts(accessToken);
      if (accounts.isEmpty()) {
        throw new RuntimeException("No accounts found for the user.");
      }

      String accountUid =
          accounts.stream()
              .filter(account -> "PRIMARY".equalsIgnoreCase(account.accountType()))
              .map(Account::accountUid)
              .findFirst()
              .orElseThrow(() -> new RuntimeException("Primary account not found."));

      String savingsGoalUid = savingGoalService.ensureSavingsGoal(accountUid);
      savingGoalService.addToSavingsGoal(accountUid, savingsGoalUid, totalRoundUp, "GBP");

      Map<String, Object> response = new HashMap<>();
      response.put("status", HttpStatus.OK.value());
      response.put("message", "Round-up performed successfully.");
      response.put("roundUpTotalMinorUnits", totalRoundUp);
      response.put("savingsGoalName", "Round-up Savings Goal");
      response.put("savingsGoalUid", savingsGoalUid);
      response.put("currency", "GBP");
      response.put("timestamp", System.currentTimeMillis());

      return ResponseEntity.ok(response);
    } catch (Exception e) {
      throw new RuntimeException("Error performing round-up: " + e.getMessage(), e);
    }
  }
}