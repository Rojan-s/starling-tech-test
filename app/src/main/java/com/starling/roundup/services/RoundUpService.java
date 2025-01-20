package com.starling.roundup.services;

import com.starling.roundup.components.Transactions;
import java.util.List;
import org.springframework.stereotype.Service;

/** Service to calculate round-up amounts from transactions. */
@Service
public class RoundUpService {

  /**
   * Calculates the total round-up amount for a list of transactions.
   *
   * @param transactions List of transactions.
   * @return Total round-up value in minor units.
   */
  public int calculateRoundUp(List<Transactions> transactions) {
    int roundUpTotal = 0;

    for (Transactions txn : transactions) {
      int amount = txn.amount();
      int roundUp = 100 - (amount % 100); // Calculate difference to next whole unit
      if (roundUp != 100) {
        roundUpTotal += roundUp;
      }
    }

    return roundUpTotal;
  }
}
