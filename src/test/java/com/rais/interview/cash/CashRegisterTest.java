package com.rais.interview.cash;

import com.rais.interview.cash.common.Cash;
import com.rais.interview.cash.common.Constant;
import com.rais.interview.cash.exception.ApplicationException;
import com.rais.interview.cash.exception.InvalidTransactionException;
import com.rais.interview.cash.service.CashRegister;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * The type Cash register test.
 */
public class CashRegisterTest {
  private CashRegister cashRegister;

  /**
   * Sets up.
   */
  @Before
  public void setUp() {
    cashRegister = new CashRegister();
  }

  /**
   * Test add cash.
   */
  @Test
  public void testAddCash() {
    Cash cash = new Cash(2, 3, 4, 5, 6);
    cashRegister.addCash(cash);

    // Verify the cash register contains the correct amounts
    Assert.assertEquals(2, cashRegister.getNoteCount("20"));
    Assert.assertEquals(3, cashRegister.getNoteCount("10"));
    Assert.assertEquals(4, cashRegister.getNoteCount("5"));
    Assert.assertEquals(5, cashRegister.getNoteCount("2"));
    Assert.assertEquals(6, cashRegister.getNoteCount("1"));
  }

  /**
   * Test remove cash.
   */
  @Test
  public void testRemoveCash() {
    // Add some initial cash to the register
    Cash initialCash = new Cash(2, 3, 4, 5, 6);
    cashRegister.addCash(initialCash);

    Cash cashToRemove = new Cash(1, 2, 3, 4, 5);
    cashRegister.removeCash(cashToRemove);

    // Verify the cash register contains the correct amounts after removal
    Assert.assertEquals(1, cashRegister.getNoteCount("20"));
    Assert.assertEquals(1, cashRegister.getNoteCount("10"));
    Assert.assertEquals(1, cashRegister.getNoteCount("5"));
    Assert.assertEquals(1, cashRegister.getNoteCount("2"));
    Assert.assertEquals(1, cashRegister.getNoteCount("1"));
  }

  /**
   * Test process payment.
   *
   * @throws ApplicationException the application exception
   */
  @Test
  public void testProcessPayment() throws ApplicationException {
    // Add cash to the register
    Cash initialCash = new Cash(10, 10, 10, 10, 10);
    cashRegister.addCash(initialCash);

    // Perform a payment transaction
    int billedAmount = 19;
    Cash cashPaid = new Cash(0, 2, 1, 0, 1);

    Cash returnedCash = cashRegister.processPayment(billedAmount, cashPaid);

    // Verify the cash register contains the correct amounts after the transaction
    Assert.assertEquals(10, cashRegister.getNoteCount(Constant.TWENTY));
    Assert.assertEquals(12, cashRegister.getNoteCount(Constant.TEN));
    Assert.assertEquals(10, cashRegister.getNoteCount(Constant.FIVE));
    Assert.assertEquals(9, cashRegister.getNoteCount(Constant.TWO));
    Assert.assertEquals(11, cashRegister.getNoteCount(Constant.ONE));

    // Verify the returned cash is correct
    Assert.assertEquals(0, returnedCash.getTwenty());
    Assert.assertEquals(0, returnedCash.getTen());
    Assert.assertEquals(1, returnedCash.getFive());
    Assert.assertEquals(1, returnedCash.getTwo());
    Assert.assertEquals(0, returnedCash.getOne());
  }

  /**
   * Test process payment invalid transaction.
   *
   * @throws ApplicationException the application exception
   */
  @Test(expected = ApplicationException.class)
  public void testProcessPaymentInvalidTransaction() throws ApplicationException {
    // Add cash to the register
    Cash initialCash = new Cash(10, 10, 10, 10, 10);
    cashRegister.addCash(initialCash);

    // Perform a payment transaction with an invalid billed amount
    int billedAmount = 1000;
    Cash cashPaid = new Cash(0, 0, 0, 0, 100);

    cashRegister.processPayment(billedAmount, cashPaid);
  }

  /**
   * Test calculate total cash.
   */
  @Test
  public void testCalculateTotalCash() {
    Cash cash = new Cash(2, 3, 4, 5, 6);
    int totalCash = cashRegister.calculateTotalCash(cash);

    // Verify the total cash amount is calculated correctly
    int expectedTotalCash = 2 * 20 + 3 * 10 + 4 * 5 + 5 * 2 + 6;
    Assert.assertEquals(expectedTotalCash, totalCash);
  }

  /**
   * Test generate denomination cash to be returned.
   *
   * @throws InvalidTransactionException the invalid transaction exception
   */
  @Test(expected = InvalidTransactionException.class)
  public void testGenerateDenominationCashToBeReturned() throws InvalidTransactionException {
    int amountToBeReturned = 87;
    Cash returnCash = cashRegister.generateDenominationCashToBeReturned(amountToBeReturned);

    // Verify the generated denomination cash is correct
    Assert.assertEquals(4, returnCash.getTwenty());
    Assert.assertEquals(1, returnCash.getTen());
    Assert.assertEquals(1, returnCash.getFive());
    Assert.assertEquals(1, returnCash.getTwo());
    Assert.assertEquals(0, returnCash.getOne());
  }

  /**
   * Test generate denomination cash to be returned insufficient notes.
   *
   * @throws InvalidTransactionException the invalid transaction exception
   */
  @Test(expected = InvalidTransactionException.class)
  public void testGenerateDenominationCashToBeReturnedInsufficientNotes()
      throws InvalidTransactionException {
    int amountToBeReturned = 200;
    cashRegister.generateDenominationCashToBeReturned(amountToBeReturned);
  }

  /**
   * Test display balance.
   */
  @Test
  public void testDisplayBalance() {
    // Add cash to the register
    Cash cash = new Cash(2, 3, 4, 5, 6);
    cashRegister.addCash(cash);

    // Redirect system out to capture the output
    ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    System.setOut(new PrintStream(outContent));

    // Display the balance
    cashRegister.displayBalance();

    // Restore the original system out
    System.setOut(System.out);

    // Verify the displayed balance is correct
    String expectedOutput =
        "$1: 6" + System.lineSeparator() + "$2: 5" + System.lineSeparator() + "$5: 4"
            + System.lineSeparator() + "$20: 2" + System.lineSeparator() + "$10: 3"
            + System.lineSeparator() + "Total Balance Amount: $106" + System.lineSeparator();
    Assert.assertEquals(expectedOutput, outContent.toString());
  }


}

