package com.rais.interview.cash.exception;

/**
 * The type Application exception.
 */
public class ApplicationException extends Exception {
  /**
   * Instantiates a new Application exception.
   *
   * @param exception the exception
   */
  public ApplicationException(InvalidTransactionException exception) {
    super(exception);
  }
}
