package org.galatea.starter.service.object;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Data;
import org.galatea.starter.domain.AvStockDay;
import org.galatea.starter.object.StockDay;

@Builder
@Data
public class AvResponse {

  @JsonProperty("Meta Data")
  private AvMetaData metaData;
  @JsonProperty(value = "Time Series (Daily)", access = JsonProperty.Access.WRITE_ONLY)
  private Map<LocalDate, AvStockDay> symbols;

  /**
   * Returns the results from this api call as a {@link StockDay} list.
   */
  public List<StockDay> resultsAsList(int stockId) {
    final ZoneOffset offset = ZoneOffset.ofHours(-5);
    List<StockDay> results = new ArrayList<>();
    LocalDate latestUpdate = getMetaData().getLastRefresh().toLocalDate();
    LocalTime updateTime = getMetaData().getLastRefresh().toLocalTime();

    getSymbols().forEach((date, data) -> {
      StockDay day = StockDay.builder()
          .stockId(stockId)
          .open(data.getOpen())
          .high(data.getHigh())
          .low(data.getLow())
          .close(data.getClose())
          .volume(data.getVolume())
          .eventDate(date.atTime(OffsetTime.of(0, 0, 0, 0, offset)))
          .updateTime(date.isEqual(latestUpdate) ? updateTime : null)
          .build();
      results.add(day);
    });
    return results;
  }
}
