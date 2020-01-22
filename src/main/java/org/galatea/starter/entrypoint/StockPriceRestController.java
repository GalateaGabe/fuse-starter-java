package org.galatea.starter.entrypoint;

import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityManagerFactory;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.aspect4log.Log;
import net.sf.aspect4log.Log.Level;
import org.galatea.starter.database.DatabaseConnectionHandler;
import org.galatea.starter.object.StockSymbol;
import org.galatea.starter.service.AvService;
import org.galatea.starter.service.object.AvResponse;
import org.galatea.starter.service.object.StockDataResponse;
import org.galatea.starter.service.object.StockRequestMetaData;
import org.galatea.starter.utils.DateTimeUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Log(enterLevel = Level.INFO, exitLevel = Level.INFO)
@Validated
@RestController
@RequiredArgsConstructor
public class StockPriceRestController {

  @NonNull
  private AvService avService;
  @Autowired
  private EntityManagerFactory entityManagerFactory;


  private Session getCurrentSession() {
    return entityManagerFactory.unwrap(SessionFactory.class).openSession();
  }

  /**
   * Returns a list of stock prices for the given date range. Pulls from database first, but will
   * fetch from AV if local data is insufficient.
   *
   * @param symbol Stock symbol, ie "MSFT"
   * @param days # of days to fetch for, ie 25
   */
  @GetMapping(value = "${mvc.av.getDailyTimeSeries}", produces = {MediaType.APPLICATION_JSON_VALUE})
  public StockDataResponse getRecentStockPrice(@RequestParam(name = "stock") final String symbol,
      @RequestParam(name = "days") final int days) {
    try (Session session = getCurrentSession();) {
      final Instant start = Instant.now();
      final LocalDate startOfToday = LocalDate.now();
      final LocalDate startOfRange = startOfToday.minusDays(days + 1L);
      final ZoneOffset zone = ZoneOffset.ofHours(-5);

      StockRequestMetaData metaData = new StockRequestMetaData();
      metaData.setSymbol(symbol);
      metaData
          .setRequestTime(OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
      String cacheStatus;
      int stockId = DatabaseConnectionHandler.selectOrInsertStock(symbol, session);

      //having this data as both a map
      List<StockSymbol> stockDataList =
          DatabaseConnectionHandler.getStockData(stockId, startOfRange.atStartOfDay().atOffset(
              zone), session);

      //if the list is empty, or there are days missing from the request, we have to fetch all days.
      //TODO - ideally we would check whether or not the missing days are within the short fetch range
      boolean hasMissingDays = stockDataList.isEmpty()
          || DateTimeUtils.missingDays(startOfRange, startOfToday, stockDataList);
      if (hasMissingDays) {
        //the size for a compact call is 100 days worth of data, so only order full if needed.
        AvResponse response = avService.getAlphaVantageStockData(symbol, days > 100);
        StockSymbol.updateAll(stockDataList, stockId, response);
        DatabaseConnectionHandler.updateStockRecords(stockDataList, stockId, session);
        cacheStatus = "missing days in cache were added during this request.";
      } else {
        //filter list to any days that have incomplete stock data, excluding today
        //ie - were acquired mid-day on a business day
        List<StockSymbol> requireUpdates =
            stockDataList.stream().filter(s ->
                DateTimeUtils.duringBusinessHours(s.getUpdateTime())
                    && s.getTradeDate().toLocalDate().isBefore(startOfToday)
            ).collect(Collectors.toList());

        //if there are any days with incomplete data, get a new fresh list and update them.
        if (!requireUpdates.isEmpty()) {
          AvResponse response = avService.getAlphaVantageStockData(symbol, days > 100);
          StockSymbol.updateAll(requireUpdates, stockId, response);
          DatabaseConnectionHandler.updateStockRecords(requireUpdates, stockId, session);
          //otherwise, if the stock market is still open (or was open the last
          //time we checked), get new data for today.
          cacheStatus = "cache data was added / updated during this request.";
        } else {
          StockSymbol latest = stockDataList.get(0);
          //if there's a request from today, and today's request
          // is from business hours, then we have to update the data
          if (!latest.getTradeDate().toLocalDate().isBefore(startOfToday) && DateTimeUtils
              .duringBusinessHours(latest.getUpdateTime())) {
            StockSymbol refresh = avService.getStockPricesForRange(symbol, false).get(0);
            latest.update(refresh);
            DatabaseConnectionHandler.updateStockRecord(latest, stockId, session);
            cacheStatus =
                "data for today has been updated as of " + latest.getUpdateTime() + ".";
          } else {
            cacheStatus = "all data in this request has come from the local cache.";

          }
        }
      }

      //filter out any remaining days before the start date
      stockDataList =
          stockDataList.stream()
              .filter(stock -> !stock.getTradeDate().toLocalDate().isBefore(startOfRange))
              .collect(Collectors.toList());
      //session.close doesn't do this automatically?!
      session.clear();
      Collections.sort(stockDataList);
      Collections.reverse(stockDataList);

      final Instant end = Instant.now();
      metaData.addMessage("days", "there were ",days - stockDataList.size(), " non-business days in the requested range.");
      metaData.addMessage("time", "the request took ", start.toEpochMilli() - end.toEpochMilli(), "ms to complete.");


      return new StockDataResponse(metaData, stockDataList);
    }
  }


}
