package org.galatea.starter.entrypoint;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.aspect4log.Log;
import net.sf.aspect4log.Log.Level;
import org.galatea.starter.exception.StockSymbolNotFoundException;
import org.galatea.starter.object.StockDay;
import org.galatea.starter.repository.StockPriceRepository;
import org.galatea.starter.service.AvService;
import org.galatea.starter.service.StockPriceService;
import org.galatea.starter.service.object.StockDataResponse;
import org.galatea.starter.service.object.StockRequestMetaData;
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
  private StockPriceRepository repo;

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

    final long start = System.nanoTime();
    final StockRequestMetaData metaData = new StockRequestMetaData();
    List<StockDay> stockDataList;

    metaData.setSymbol(symbol);

    if (days > 0) {
      metaData
          .setRequestTime(OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
      try {
        stockDataList = StockPriceService.getHistoricalStockData(symbol, repo, avService, days);
        metaData.addMessage("days", "", days - stockDataList.size(),
            " day(s) in the requested range had no activity.");
      } catch (StockSymbolNotFoundException ex) {
        metaData.addMessage("error", "no stock with the symbol ", ex.getSymbol(), " found.");
        stockDataList = Collections.emptyList();
      }
    } else {
      metaData.addMessage("error", "days must be a positive integer.");
      stockDataList = Collections.emptyList();
    }
    final long end = System.nanoTime();
    metaData.addMessage("time", "the request took ", (end - start) / 1000000,
        "ms to complete.");
    return new StockDataResponse(metaData, stockDataList);
  }


}
