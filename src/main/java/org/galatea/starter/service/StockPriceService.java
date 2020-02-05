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

    List<StockDay> stockList;

    Integer stockId = repo.findStockIdBySymbol(symbol);
    if (stockId == null) {
      repo.insertStockRecord(symbol);
      stockId = repo.findStockIdBySymbol(symbol);
      stockList = new ArrayList<>();
    } else {
      OffsetDateTime offsetStart = startOfRange.atStartOfDay().atOffset(zone);
      //fetch historical info on stock from the database, up to _days_ ago
      stockList = repo.findStockHistoryById(stockId, offsetStart);
    }

    //if the list is empty, or there are days missing from the request,
    // we have to fetch all days.
    final boolean hasMissingDays = stockList.isEmpty()
        || DateTimeUtils.missingDays(startOfRange, startOfToday, stockList);
    if (hasMissingDays) {
      //the size for a compact call is 100 days worth of data, so only order full if needed.
      stockList = orderStockList(symbol, stockId, days > 100, avService);
      repo.saveAll(stockList);
    } else {
      //check if any of the data from the database contains partial data
      boolean requiresUpdates =
          stockList.stream().anyMatch(s ->
              DateTimeUtils.duringBusinessHours(s.getUpdateTime())
                  && s.getEventDate().toLocalDate().isBefore(startOfToday)
          );

      //if there are any days with incomplete data, get a new fresh list and update them.
      if (requiresUpdates) {
        stockList = orderStockList(symbol, stockId, days > 100, avService);
        repo.saveAll(stockList);
        //otherwise, if the stock market is still open (or was open the last
        //time we checked), get new data for today.
      } else {
        final StockDay latest = stockList.get(0);
        //if there's a request from today, and today's request
        // is from business hours, then we have to update the data
        if (!latest.getEventDate().toLocalDate().isBefore(startOfToday) && DateTimeUtils
            .duringBusinessHours(latest.getUpdateTime())) {
          final StockDay refresh = getMostRecentDay(symbol, avService);

          latest.update(refresh);
          repo.save(latest);
        }
      }
    }
    //filter out any remaining days before the start date, sort descending, and return.
    return stockList.stream().filter(stock ->
        stock.getEventDate().toLocalDate().isAfter(startOfRange)
    ).sorted(Comparator.reverseOrder()).collect(Collectors.toList());

  }

  private static StockDay getMostRecentDay(final String symbol, final AvService avService)
      throws StockSymbolNotFoundException {
    List<StockDay> days = orderStockList(symbol, 0, false, avService);
    if (!days.isEmpty()) {
      return days.get(0);
    } else {
      throw new StockSymbolNotFoundException(symbol);
    }
  }

  /**
   * get a new list from alphavantage and write the results to the database. any existing entries
   * should be updated.
   */
  private static List<StockDay> orderStockList(final String symbol, final Integer stockId,
      final boolean full, final AvService avService)
      throws StockSymbolNotFoundException {

    final AvResponse response = avService.getAlphaVantageStockData(symbol, full);

    return response.resultsAsList(stockId);
  }
}

