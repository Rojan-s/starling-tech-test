package com.starling.roundup.services;

import static com.starling.roundup.services.Constants.BASE_URL;
import static com.starling.roundup.utils.TransactionParser.parseTransactions;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.starling.roundup.components.Account;
import com.starling.roundup.components.Transactions;
import com.starling.roundup.utils.HttpUtils;
import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/** Service for handling transaction-related operations. */
@Service
public class TransactionService {

  @Value("${access.token}")
  private String accessToken;

  private final AccountsService accountsService;

  /**
   * Constructs a TransactionService with the provided AccountsService.
   *
   * @param accountsService Service to manage account-related operations.
   */
  public TransactionService(AccountsService accountsService) {
    this.accountsService = accountsService;
  }

  /**
   * Retrieves transactions between the specified start date and end date.
   *
   * @param startDate Start date for fetching transactions.
   * @return A list of transactions.
   */
  public List<Transactions> getTransactionsBetweenDates(LocalDate startDate) {
    List<Account> accounts = accountsService.getAllAccounts(accessToken);
    if (accounts.isEmpty()) {
      throw new RuntimeException("No accounts found for the user.");
    }

    String accountUid = null;
    String categoryUid = null;

    for (Account account : accounts) {
      if ("PRIMARY".equalsIgnoreCase(account.accountType())) {
        accountUid = account.accountUid();
        categoryUid = account.defaultCategory();
        break;
      }
    }

    if (accountUid == null || categoryUid == null) {
      throw new RuntimeException("Primary account or category not found.");
    }

    String url = buildTransactionUrl(accountUid, categoryUid, startDate);
    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders headers = HttpUtils.createHeaders(accessToken);
    HttpEntity<String> entity = new HttpEntity<>(headers);

    try {
      ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity,
          String.class);
      JsonObject root = JsonParser.parseString(response.getBody()).getAsJsonObject();
      JsonArray transactionsArray = root.getAsJsonArray("feedItems");
      return parseTransactions(transactionsArray);

    } catch (Exception e) {
      throw new RuntimeException("Error fetching transactions: " + e.getMessage(), e);
    }
  }

  /**
   * Constructs the transaction URL with the required parameters.
   *
   * @param accountUid  The UID of the account.
   * @param categoryUid The UID of the category.
   * @param startDate   The start date for the transaction query.
   * @return A fully constructed URL for fetching transactions.
   */
  private String buildTransactionUrl(String accountUid, String categoryUid, LocalDate startDate) {
    LocalDate endDate = startDate.plusDays(6);
    return String.format(
        "%s/api/v2/feed/account/%s/category/%s/transactions-between?"
            + "minTransactionTimestamp=%s"
            + "&maxTransactionTimestamp=%s",
        BASE_URL,
        accountUid,
        categoryUid,
        startDate + "T00:00:00.000Z",
        endDate + "T23:59:59.999Z");
  }
}
