package org.galatea.starter.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.galatea.starter.exception.StockSymbolNotFoundException;
import org.galatea.starter.service.object.AvResponse;
import org.springframework.stereotype.Service;

/**
 * A layer for transformation, aggregation, and business required when retrieving data from
 * AlphaVantage.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AvService {

  @NonNull
  private AvClient avClient;


  /**
   * get a  response from alphavantage, including metadata and last trade time.
   *
   * @param symbol stock symbol to get the data for
   * @param full full data or just last 100 business days
   * @throws StockSymbolNotFoundException if the given symbol does not correspond to any stock *
   *     per the api.
   */
  public AvResponse getAlphaVantageStockData(final @NonNull String symbol, final boolean full)
      throws StockSymbolNotFoundException {
    AvResponse response;
    if (full) {
      response = avClient.getDailyTimeSeries(symbol);
    } else {
      response = avClient.getDailyTimeSeriesCompact(symbol);
    }
    if (response == null || response.getMetaData() == null || response.getSymbols() == null) {
      throw new StockSymbolNotFoundException(symbol);
    } else {
      return response;
    }
  }
}

