package org.galatea.starter.service;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.NonNull;
import org.galatea.starter.exception.StockSymbolNotFoundException;
import org.galatea.starter.object.StockDay;
import org.galatea.starter.repository.StockPriceRepository;
import org.galatea.starter.service.object.AvResponse;
import org.galatea.starter.utils.DateTimeUtils;

public class StockPriceService {

  private StockPriceService() {}

  static final ZoneOffset zone = ZoneOffset.ofHours(-5);

  /**
   * Queries the database to check whether this information exists yet. If it doesn't, the
   * alphavantage api is queried to return data for the requested range. all data from the api will
   * then be captured and stored locally.
   *
   * @param symbol symbol of the stock, ex 'MSFT'
   * @param repo database connection
   * @param avService alphavantage connection
   * @param days how many days back to collect data from
   * @return List of {@link StockDay} objects, for each business day up to <code>days</code> ago.
   */
  public static List<StockDay> getHistoricalStockData(final String symbol,
      final StockPriceRepository repo, final AvService avService, final @NonNull Integer days)
      throws StockSymbolNotFoundException {

    final LocalDate startOfToday = LocalDate.now();
    final LocalDate startOfRange = startOfToday.minusDays(days);

    List<StockDay> stockDataList;

    Integer stockId = repo.findStockIdBySymbol(symbol);
    if (stockId == null) {
      repo.insertStockRecord(symbol);
      stockId = repo.findStockIdBySymbol(symbol);
      stockDataList = new ArrayList<>();
    } else {
      OffsetDateTime offsetStart = startOfRange.atStartOfDay().atOffset(zone);
      stockDataList = repo.findStockHistoryById(stockId, offsetStart);
    }

    //if the list is empty, or there are days missing from the request,
    // we have to fetch all days.
    final boolean hasMissingDays = stockDataList.isEmpty()
        || DateTimeUtils.missingDays(startOfRange, startOfToday, stockDataList);
    if (hasMissingDays) {
      //the size for a compact call is 100 days worth of data, so only order full if needed.
      refreshStockList(stockDataList, symbol, stockId, days > 100, repo, avService);

    } else {
      //filter list to any days that have incomplete stock data, excluding today
      //ie - were acquired mid-day on a business day
      final List<StockDay> requireUpdates =
          stockDataList.stream().filter(s ->
              DateTimeUtils.duringBusinessHours(s.getUpdateTime())
                  && s.getTradeDate().toLocalDate().isBefore(startOfToday)
          ).collect(Collectors.toList());

      //if there are any days with incomplete data, get a new fresh list and update them.
      if (!requireUpdates.isEmpty()) {
        refreshStockList(requireUpdates, symbol, stockId, days > 100, repo, avService);

        //otherwise, if the stock market is still open (or was open the last
        //time we checked), get new data for today.
      } else {
        final StockDay latest = stockDataList.get(0);
        //if there's a request from today, and today's request
        // is from business hours, then we have to update the data
        if (!latest.getTradeDate().toLocalDate().isBefore(startOfToday) && DateTimeUtils
            .duringBusinessHours(latest.getUpdateTime())) {
          final StockDay refresh = avService.getStockPricesForRange(symbol, false).get(0);

          latest.update(refresh);
          repo.save(latest);
        }
      }
    }
    //filter out any remaining days before the start date, sort descending, and return.
    return stockDataList.stream().filter(stock ->
        stock.getTradeDate().toLocalDate().isAfter(startOfRange)
    ).sorted(Comparator.reverseOrder()).collect(Collectors.toList());

  }

  /**
   * Update a list (or sublist) of stocks with data from alphavantage. the parameter list will be
   * modified to update any partial or missing entries.
   */
  private static List<StockDay> refreshStockList(final List<StockDay> stockDataList,
      final String symbol, final Integer stockId, final boolean full,
      final StockPriceRepository repo, final AvService avService)
      throws StockSymbolNotFoundException {

    final AvResponse response = avService.getAlphaVantageStockData(symbol, full);
    StockDay.updateAll(stockDataList, stockId, response.getSymbols());

    return repo.saveAll(stockDataList);
  }
}

