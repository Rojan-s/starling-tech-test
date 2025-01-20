package com.starling.roundup.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.starling.roundup.components.Transactions;
import java.util.ArrayList;
import java.util.List;

/** Utility class for parsing transaction-related JSON data. */
public class TransactionParser {

  /**
   * Parses a JSON array of transactions into a list of Transactions objects.
   *
   * @param transactionsArray The JSON array containing transaction data.
   * @return A list of parsed Transactions objects.
   */
  public static List<Transactions> parseTransactions(JsonArray transactionsArray) {
    List<Transactions> transactions = new ArrayList<>();
    for (JsonElement element : transactionsArray) {
      try {
        JsonObject transactionObj = element.getAsJsonObject();
        String transactionUid = transactionObj.get("feedItemUid").getAsString();
        int amount = transactionObj.get("amount").getAsJsonObject().get("minorUnits").getAsInt();
        String transactionTime = transactionObj.get("transactionTime").getAsString();
        String direction = transactionObj.get("direction").getAsString();
        String status = transactionObj.get("status").getAsString();
        String source = transactionObj.get("source").getAsString();
        String currency =
            transactionObj.get("amount").getAsJsonObject().get("currency").getAsString();

        if (transactionUid != null
            && transactionTime != null
            && "OUT".equals(direction)
            && "SETTLED".equals(status)
            && "GBP".equals(currency)
            && !"INTERNAL_TRANSFER".equals(source)) {
          transactions.add(new Transactions(amount));
        } else {
          System.err.println("Skipping transaction: " + transactionObj);
        }
      } catch (Exception e) {
        System.err.println("Error parsing transaction: " + element);
      }
    }
    return transactions;
  }
}