package org.galatea.starter.exception;

import lombok.Getter;

public class StockSymbolNotFoundException extends Exception {

  @Getter
  private final String symbol;

  /**
   * Thrown when the AV api does not think a stock exists with this symbol.
   */
  public StockSymbolNotFoundException(final String symbol) {
    this(symbol, null);
  }

  /**
   * Thrown when the AV api does not think a stock exists with this symbol.
   */
  public StockSymbolNotFoundException(final String symbol,final  Throwable throwable) {
    super("No data for stock found with symbol: " + symbol, throwable);
    this.symbol = symbol;
  }
}
