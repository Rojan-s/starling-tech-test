package com.starling.roundup.services;

import static com.starling.roundup.services.Constants.BASE_URL;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.starling.roundup.utils.HttpUtils;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/** Service for managing savings goals. */
@Service
public class SavingGoalService {

  @Value("${access.token}")
  private String accessToken;

  private final RestTemplate restTemplate = new RestTemplate();

  /**
   * Ensures the "Round-up Savings Goal" exists. Creates it if necessary.
   *
   * @param accountUid The account UID.
   * @return The UID of the savings goal.
   */
  public String ensureSavingsGoal(String accountUid) {
    String url = String.format("%s/api/v2/account/%s/savings-goals", BASE_URL, accountUid);

    HttpHeaders headers = HttpUtils.createHeaders(accessToken);
    HttpEntity<String> entity = new HttpEntity<>(headers);

    ResponseEntity<String> response =
        restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
    JsonObject root = JsonParser.parseString(response.getBody()).getAsJsonObject();

    if (root.has("savingsGoalList")) {
      for (var goal : root.getAsJsonArray("savingsGoalList")) {
        JsonObject goalObject = goal.getAsJsonObject();
        if (goalObject.get("name").getAsString().equals("Round-up Savings Goal")) {
          return goalObject.get("savingsGoalUid").getAsString();
        }
      }
    }

    return createSavingsGoal(accountUid);
  }

  /**
   * Creates a new "Round-up Savings Goal".
   *
   * @param accountUid The account UID.
   * @return The UID of the newly created savings goal.
   */
  private String createSavingsGoal(String accountUid) {
    JsonObject requestBody = new JsonObject();
    requestBody.addProperty("name", "Round-up Savings Goal");
    requestBody.addProperty("currency", "GBP");

    HttpHeaders headers = HttpUtils.createHeaders(accessToken);
    HttpEntity<String> entity = new HttpEntity<>(requestBody.toString(), headers);
    String url = String.format("%s/api/v2/account/%s/savings-goals", BASE_URL, accountUid);
    ResponseEntity<String> response =
        restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);

    JsonObject root = JsonParser.parseString(response.getBody()).getAsJsonObject();
    return root.get("savingsGoalUid").getAsString();
  }

  /**
   * Adds a round-up amount to the savings goal.
   *
   * @param accountUid The account UID.
   * @param savingsGoalUid The UID of the savings goal.
   * @param roundUpAmount The round-up amount in minor units (e.g., pence for GBP).
   * @param currency The currency of the amount (e.g., "GBP").
   */
  public void addToSavingsGoal(
      String accountUid, String savingsGoalUid, long roundUpAmount, String currency) {
    JsonObject requestBody = new JsonObject();
    JsonObject amountObject = new JsonObject();
    amountObject.addProperty("currency", currency);
    amountObject.addProperty("minorUnits", roundUpAmount);
    requestBody.add("amount", amountObject);

    HttpHeaders headers = HttpUtils.createHeaders(accessToken);

    HttpEntity<String> entity = new HttpEntity<>(requestBody.toString(), headers);

    String transferUid = UUID.randomUUID().toString();
    String url = String.format("%s/api/v2/account/%s/savings-goals/%s/add-money/%s",
        BASE_URL, accountUid, savingsGoalUid, transferUid);
    restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);
  }
}
