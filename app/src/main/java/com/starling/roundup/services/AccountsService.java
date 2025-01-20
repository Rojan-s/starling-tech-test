package com.starling.roundup.services;

import static com.starling.roundup.services.Constants.BASE_URL;
import static com.starling.roundup.utils.AccountParser.parseAccounts;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.starling.roundup.components.Account;
import com.starling.roundup.utils.HttpUtils;
import java.util.List;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/** Service for handling account-related operations. */
@Service
public class AccountsService {

  /**
   * Retrieves all accounts for the user.
   *
   * @param accessToken The access token for authorization.
   * @return A list of accounts.
   */
  public List<Account> getAllAccounts(String accessToken) {
    String url = BASE_URL + "/api/v2/accounts";
    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders headers = HttpUtils.createHeaders(accessToken);
    HttpEntity<String> entity = new HttpEntity<>(headers);

    try {
      ResponseEntity<String> response =
          restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

      JsonObject root = JsonParser.parseString(response.getBody()).getAsJsonObject();
      JsonArray accountsArray = root.getAsJsonArray("accounts");

      return parseAccounts(accountsArray);

    } catch (Exception e) {
      throw new RuntimeException("Error fetching accounts: " + e.getMessage(), e);
    }
  }
}
