package org.galatea.starter.object;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.galatea.starter.service.object.AvResponse;
import org.junit.Test;

public class StockDayTest {

  private static final ZoneOffset zone = ZoneOffset.ofHours(-5);

  @Test
  public void updateAll_Test() {
    int capacity = 30;
    int stockId = 1;
    List<StockDay> days1 = new ArrayList<>();
    Map<LocalDate, StockDay> days2 = new HashMap<>();
    LocalDate start = LocalDate.of(2000, 1, 1);
    //generate 30 random new days1
    for (int i = 0; i < capacity; i++) {
      final BigDecimal open = BigDecimal.valueOf(Math.floor(Math.random() * 100000) / 100);
      final BigDecimal high = BigDecimal.valueOf(Math.floor(Math.random() * 100000) / 100);
      final BigDecimal low = BigDecimal.valueOf(Math.floor(Math.random() * 100000) / 100);
      final BigDecimal close = BigDecimal.valueOf(Math.floor(Math.random() * 100000) / 100);
      final long volume = (long) (Math.random() * 100000);

      StockDay day = new StockDay.StockDayBuilder()
          .stockId(stockId)
          .open(open).high(high).low(low).close(close)
          .volume(volume)
          .tradeDate(start.plusDays(i).atStartOfDay().atOffset(zone))
          .updateTime(LocalTime.of(11, 22, 33))
          .build();
      days1.add(day);
    }
    //then create a map of updates
    days1.forEach(day -> {
      final BigDecimal open = BigDecimal.valueOf(Math.floor(Math.random() * 100000) / 100);
      final BigDecimal high = BigDecimal.valueOf(Math.floor(Math.random() * 100000) / 100);
      final BigDecimal low = BigDecimal.valueOf(Math.floor(Math.random() * 100000) / 100);
      final BigDecimal close = BigDecimal.valueOf(Math.floor(Math.random() * 100000) / 100);
      final long volume = (long) (Math.random() * 100000);

      StockDay update = new StockDay.StockDayBuilder()
          .stockId(stockId)
          .open(open).high(high).low(low).close(close)
          .volume(volume)
          .tradeDate(day.getTradeDate())
          .updateTime(day.getUpdateTime().plusHours(2))
          .build();

      days2.put(day.getTradeDate().toLocalDate(), update);
    });

    AvResponse response = AvResponse.builder().symbols(days2).build();

    //perform the action we're actually testing
    StockDay.updateAll(days1, stockId, response);

    //there should be days here
    assertFalse(days1.isEmpty());
    //all update days should be used at this point
    assertTrue(days2.isEmpty());
  }

  @Test
  public void update_Test() {
    int stockId = 42;
    StockDay initDay = new StockDay.StockDayBuilder()
        .stockId(stockId)
        .open(new BigDecimal("170.43"))
        .high(new BigDecimal("174.5"))
        .low(new BigDecimal("170.4"))
        .close(new BigDecimal("174.38"))
        .volume(30094894)
        .tradeDate(OffsetDateTime.of(2020, 2, 3, 0, 0, 0, 0, zone))
        .updateTime(LocalTime.of(11, 22, 33))
        .build();
    StockDay updateDay = new StockDay.StockDayBuilder()
        .stockId(stockId)
        .open(new BigDecimal("190.43"))
        .high(new BigDecimal("194.5"))
        .low(new BigDecimal("190.4"))
        .close(new BigDecimal("194.38"))
        .volume(48467215)
        .tradeDate(OffsetDateTime.of(2020, 2, 6, 2, 3, 4, 5, zone))
        .updateTime(LocalTime.of(0, 0, 0))
        .build();

    initDay.update(updateDay);

    assertEquals(initDay.getStockId(), updateDay.getStockId());
    assertEquals(initDay.getOpen(), updateDay.getOpen());
    assertEquals(initDay.getHigh(), updateDay.getHigh());
    assertEquals(initDay.getLow(), updateDay.getLow());
    assertEquals(initDay.getClose(), updateDay.getClose());
    assertEquals(initDay.getVolume(), updateDay.getVolume());
    assertTrue(initDay.getTradeDate().isEqual(updateDay.getTradeDate()));
    assertEquals(initDay.getUpdateTime(), updateDay.getUpdateTime());
  }
}
