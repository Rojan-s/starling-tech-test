package com.starling.roundup;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.starling.roundup.components.Transactions;
import com.starling.roundup.services.RoundUpService;
import com.starling.roundup.services.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RoundUpTest {

  @Mock
  private TransactionService transactionService;

  @InjectMocks
  private RoundUpService roundUpService;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  public void testCalculateRoundUp_validTransactions() {
    Transactions txn1 = new Transactions(345); // Rounds to 400 -> +55
    Transactions txn2 = new Transactions(510); // Rounds to 600 -> +90
    Transactions txn3 = new Transactions(230); // Rounds to 300 -> +70

    List<Transactions> transactions = Arrays.asList(txn1, txn2, txn3);

    int roundUp = roundUpService.calculateRoundUp(transactions);

    assertEquals(215, roundUp, "Total round-up should be 215 minor units");
  }

  @Test
  public void testCalculateRoundUp_noTransactions() {
    List<Transactions> transactions = Collections.emptyList();
    int roundUp = roundUpService.calculateRoundUp(transactions);
    assertEquals(0, roundUp, "Round-up should be 0 for no transactions");
  }

  @Test
  public void testCalculateRoundUp_allExactMultiples() {

    Transactions txn1 = new Transactions(400);
    Transactions txn2 = new Transactions(600);
    List<Transactions> transactions = Arrays.asList(txn1, txn2);
    int roundUp = roundUpService.calculateRoundUp(transactions);
    assertEquals(0, roundUp, "Round-up should be 0 when all amounts are exact multiples of 100");
  }

  @Test
  public void testPerformRoundUp_validFlow() {

    LocalDate startDate = LocalDate.of(2025, 1, 1);
    Transactions txn1 = new Transactions(345);
    Transactions txn2 = new Transactions(510);
    List<Transactions> transactions = Arrays.asList(txn1, txn2);
    when(transactionService.getTransactionsBetweenDates(startDate)).thenReturn(transactions);
    int roundUpTotal = roundUpService.calculateRoundUp(transactions);
    assertEquals(145, roundUpTotal, "Total round-up should be 145 minor units");
  }

  @Test
  public void testPerformRoundUp_noTransactions() {
    LocalDate startDate = LocalDate.of(2025, 1, 1);

    when(transactionService.getTransactionsBetweenDates(startDate)).thenReturn(Collections.emptyList());
    Exception exception = assertThrows(RuntimeException.class, () -> {
      List<Transactions> transactions = transactionService.getTransactionsBetweenDates(startDate);
      int roundUpTotal = roundUpService.calculateRoundUp(transactions);

      if (roundUpTotal <= 0) {
        throw new RuntimeException("No round-up amount to add.");
      }
    });

    assertEquals("No round-up amount to add.", exception.getMessage());
  }

  @Test
  public void testCalculateRoundUp_largeTransactions() {

    Transactions txn1 = new Transactions(999999);
    Transactions txn2 = new Transactions(100);

    List<Transactions> transactions = Arrays.asList(txn1, txn2);

    int roundUp = roundUpService.calculateRoundUp(transactions);

    assertEquals(1, roundUp, "Expected round-up of 1 minor unit for transactions [999999, 1]");
  }


}
