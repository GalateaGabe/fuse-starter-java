package org.galatea.starter.service;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.galatea.starter.object.StockSymbol;
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
   * get (100 latest| all) days of stock data from the alpha vantage API as a list.
   *
   * @param symbol Symbol of the requested stock, ex "MSFT"
   * @param full Whether to request the full 20+ year range, or just the last 100 days
   * @return the stock data from each day in the requested range, starting with the most recent day
   *     & in descending order
   */
  public List<StockSymbol> getStockPricesForRange(final @NonNull String symbol,
      final boolean full) {
    AvResponse response = getAlphaVantageStockData(symbol, full);

    if (!response.getSymbols().isEmpty()) {
      Map<LocalDate, StockSymbol> map = response.getSymbols();
      List<StockSymbol> symbols = new ArrayList<>();
      map.forEach((date, data) -> {
        data.setTradeDate(date.atTime(OffsetTime.of(0, 0, 0, 0, ZoneOffset.ofHours(-5))));
        symbols.add(data);
      });
      LocalDate latestTrade = symbols.get(0).getTradeDate().toLocalDate();
      LocalDate refreshDate = response.getMetaData().getLastRefresh().toLocalDate();
      //if the latest trade occurred today, then update the trade date to be the refresh date so
      //that we are aware it's (potentially) incomplete
      if (latestTrade.isEqual(refreshDate)) {
        symbols.get(0).setUpdateTime(response.getMetaData().getLastRefresh().toLocalTime());
      }
      return symbols;
    } else {
      return Collections.emptyList();
    }
  }

  /**
   * get a  response from alphavantage, including metadata and last trade time.
   * @param symbol stock symbol to get the data for
   * @param full full data or just last 100 business days
   * @return
   */
  public AvResponse getAlphaVantageStockData(final @NonNull String symbol, final boolean full) {
    AvResponse response;
    if (full) {
      response = avClient.getDailyTimeSeries(symbol);
    } else {
      response = avClient.getDailyTimeSeriesCompact(symbol);
    }
    OffsetDateTime lastUpdate = response.getMetaData().getLastRefresh();
    StockSymbol today = response.getSymbols().get(lastUpdate.toLocalDate());
    if (today != null) {
      today.setUpdateTime(lastUpdate.toLocalTime());
    }
    return response;
  }
}

