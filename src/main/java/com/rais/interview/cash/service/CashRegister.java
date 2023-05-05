package com.rais.interview.cash.service;

import com.rais.interview.cash.common.Cash;
import com.rais.interview.cash.exception.ApplicationException;
import com.rais.interview.cash.exception.InvalidTransactionException;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BinaryOperator;

import static com.rais.interview.cash.common.Constant.*;

/**
 * The type Cash register.
 */
public class CashRegister {
  /**
   * The Cash register.
   */
  final Map<String, Integer> cashRegister = new HashMap<>();

  /**
   * Add cash.
   *
   * @param cash the cash
   */
  public void addCash(Cash cash) {
    updateCash(cash, Integer::sum);
  }

  /**
   * Remove cash.
   *
   * @param cash the cash
   */
  public void removeCash(Cash cash) {
    updateCash(cash, (a, b) -> a - b);
  }

  private void updateCash(Cash cash, BinaryOperator<Integer> operator) {
    cashRegister.merge(TWENTY, cash.getTwenty(), operator);
    cashRegister.merge(TEN, cash.getTen(), operator);
    cashRegister.merge(FIVE, cash.getFive(), operator);
    cashRegister.merge(TWO, cash.getTwo(), operator);
    cashRegister.merge(ONE, cash.getOne(), operator);
  }

  /**
   * Process payment cash.
   *
   * @param billedAmount the billed amount
   * @param cash the cash
   * @return the cash
   * @throws ApplicationException the application exception
   */
  public Cash processPayment(int billedAmount, Cash cash) throws ApplicationException {
    try {
      addCash(cash);
      final int amountToBeReturned = calculateAmountToReturn(billedAmount, cash);
      Cash returnedCash = generateDenominationCashToBeReturned(amountToBeReturned);
      removeCash(returnedCash);
      return returnedCash;
    } catch (InvalidTransactionException e) {
      removeCash(cash);
      throw new ApplicationException(e);
    }
  }

  private int calculateAmountToReturn(int billedAmount, Cash cash)
      throws InvalidTransactionException {
    int totalCash = calculateTotalCash(cash);
    if (totalCash < billedAmount)
      throw new InvalidTransactionException("Billing amount is greater than total paid amount");
    return calculateTotalCash(cash) - billedAmount;
  }

  /**
   * Calculate total cash int.
   *
   * @param cash the cash
   * @return the int
   */
  public int calculateTotalCash(Cash cash) {
    return cash.getTwenty() * 20 + cash.getTen() * 10 + cash.getFive() * 5 + cash.getTwo() * 2
        + cash.getOne();
  }

  /**
   * Generate denomination cash to be returned cash.
   *
   * @param amountToBeReturned the amount to be returned
   * @return the cash
   * @throws InvalidTransactionException the invalid transaction exception
   */
  public Cash generateDenominationCashToBeReturned(int amountToBeReturned)
      throws InvalidTransactionException {
    Cash returnCash = new Cash();
    returnCash.setTwenty(getMaxNumberOfNotes(TWENTY, amountToBeReturned / 20));
    amountToBeReturned %= 20;
    returnCash.setTen(getMaxNumberOfNotes(TEN, amountToBeReturned / 10));
    amountToBeReturned %= 10;
    returnCash.setFive(getMaxNumberOfNotes(FIVE, amountToBeReturned / 5));
    amountToBeReturned %= 5;
    returnCash.setTwo(getMaxNumberOfNotes(TWO, amountToBeReturned / 2));
    amountToBeReturned %= 2;
    returnCash.setOne(getMaxNumberOfNotes(ONE, amountToBeReturned));
    return returnCash;
  }

  private int getMaxNumberOfNotes(String denomination, int requiredCount)
      throws InvalidTransactionException {
    int noteCount = getNoteCount(denomination);
    if (requiredCount > noteCount) {
      throw new InvalidTransactionException(
          "We are short of " + denomination + " note. Demand is " + requiredCount + " available is "
              + noteCount);
    }
    return requiredCount;
  }

  /**
   * Display balance.
   */
  public void displayBalance() {
    cashRegister.keySet().forEach(denomination -> {
      int count = cashRegister.getOrDefault(denomination, 0);
      System.out.println("$" + denomination + ": " + count);
    });

    int totalAmount = calculateTotalAmount();
    System.out.println("Total Balance Amount: $" + totalAmount);
  }

  private int calculateTotalAmount() {
    AtomicInteger totalAmount = new AtomicInteger(0);

    cashRegister.forEach((denomination, count) -> {
      int denominationValue = Integer.parseInt(denomination);
      totalAmount.addAndGet(count * denominationValue);
    });

    return totalAmount.get();
  }

  /**
   * Gets note count.
   *
   * @param note the note
   * @return the note count
   */
  public int getNoteCount(String note) {
    return cashRegister.getOrDefault(note, 0);
  }
}
