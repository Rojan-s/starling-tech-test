package com.starling.roundup.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.starling.roundup.components.Account;
import java.util.ArrayList;
import java.util.List;

/** Utility class for parsing account-related JSON data. */
public class AccountParser {

  /**
   * Parses a JSON array of accounts into a list of Account objects.
   *
   * @param accountsArray The JSON array containing account data.
   * @return A list of parsed Account objects.
   */
  public static List<Account> parseAccounts(JsonArray accountsArray) {
    List<Account> accounts = new ArrayList<>();
    for (JsonElement element : accountsArray) {
      JsonObject accountObj = element.getAsJsonObject();
      Account account =
          new Account(
              accountObj.get("accountUid").getAsString(),
              accountObj.get("accountType").getAsString(),
              accountObj.get("defaultCategory").getAsString());
      accounts.add(account);
    }
    return accounts;
  }
}
