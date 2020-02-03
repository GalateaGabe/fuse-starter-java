package org.galatea.starter.entrypoint;

import org.galatea.starter.service.AvService;
import org.galatea.starter.service.object.StockDataResponse;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.MockServerConfigurer;

public class StockPriceRestControllerTest {

  @Autowired
  private MockServerConfigurer serverConfigurer;

  @Mock
  private AvService avService;

  @Test
  public void getRecentStockPrice_rangeCheck() {
    final int days = 30;
    final String symbol = "MSFT";
    StockDataResponse response =
        new StockPriceRestController(avService).getRecentStockPrice(symbol, days);

    assert response.getSymbolList().size() == days;
  }

  @Test
  public void getRecentStockPrice_symbolCheck() {
    final int days = 30;
    final String symbol = "MSFT";
    StockDataResponse response =
        new StockPriceRestController(avService).getRecentStockPrice(symbol, days);

    assert symbol.equals(response.getMetaData().getSymbol());
  }

}
