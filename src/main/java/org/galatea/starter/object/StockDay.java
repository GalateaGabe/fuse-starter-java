package org.galatea.starter.object;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import lombok.Data;
import org.galatea.starter.service.object.AvResponse;
import org.galatea.starter.utils.json.serialization.DateDeserializer;
import org.galatea.starter.utils.json.serialization.DateSerializer;
import org.galatea.starter.utils.json.serialization.MoneyDeserializer;

@Data
@Entity
@Table(name = "price")
@IdClass(StockSymbolId.class)
public class StockDay implements Serializable, Comparable<StockDay> {

  @Id
  @JsonIgnore
  private int stockId;
  @JsonDeserialize(using = MoneyDeserializer.class)
  @Column(name = "OpenPrice")
  @JsonAlias(value = "1. open")
  private BigDecimal open;
  @JsonDeserialize(using = MoneyDeserializer.class)
  @JsonAlias(value = "2. high")
  @Column(name = "HighPrice")
  private BigDecimal high;
  @JsonDeserialize(using = MoneyDeserializer.class)
  @JsonAlias(value = "3. low")
  @Column(name = "LowPrice")
  private BigDecimal low;
  @JsonDeserialize(using = MoneyDeserializer.class)
  @JsonAlias(value = "4. close")
  @Column(name = "ClosePrice")
  private BigDecimal close;
  @JsonAlias(value = "5. volume")
  private double volume;
  @Id
  @JsonSerialize(using = DateSerializer.class)
  @JsonDeserialize(using = DateDeserializer.class)
  @JsonProperty(value = "trade_date")
  private OffsetDateTime tradeDate;
  @JsonIgnore
  private LocalTime updateTime;

  /**
   * updates all of the stock symbols in the supplied list with data from the given update map.<br>
   *   (if a matching date is found)
   * @param sourceList the list you're updating
   * @param stockId the id the stock in the database
   * @param response your map of new data with which to update
   * @return
   */
  public static List<StockDay> updateAll(final List<StockDay> sourceList,
      final int stockId, final AvResponse response) {
    final Map<LocalDate, StockDay> updateMap = response.getSymbols();

    //update any item that exists in both
    sourceList.forEach(stock1 -> {
      final LocalDate key = stock1.getTradeDate().toLocalDate();
      final StockDay refresh = updateMap.get(key);
      if (refresh != null) {
        stock1.update(refresh);
        updateMap.remove(key);
      }
    });
    //if there are new items that weren't in the old list, append them to the list.
    if (!updateMap.isEmpty()) {
      updateMap.forEach((date, stock2) -> {
        stock2.setStockId(stockId);
        stock2.setTradeDate(date.atStartOfDay().atOffset(ZoneOffset.ofHours(-5)));
        sourceList.add(stock2);
      });

    }
    return sourceList;

  }

  /**
   * update this stock symbol with values from the given stock symbol.
   * @param other the object from which to get the new values.
   */
  public void update(final StockDay other) {
    //update anything non-null
    if (Objects.nonNull(other.getOpen())) {
      this.open = other.getOpen();
    }
    if (Objects.nonNull(other.getHigh())) {
      this.high = other.getHigh();
    }
    if (Objects.nonNull(other.getLow())) {
      this.low = other.getLow();
    }
    if (other.getVolume() != 0D) {
      this.volume = other.getVolume();
    }
    if (Objects.nonNull(other.getClose())) {
      this.close = other.getClose();
    }
    if (Objects.nonNull(other.getTradeDate())) {
      this.tradeDate = other.getTradeDate();
    }
    if (Objects.nonNull(other.getUpdateTime())) {
      this.updateTime = other.getUpdateTime();
    }
  }

  @Override
  public int compareTo(final StockDay other) {
    Objects.requireNonNull(other);
    if (this.getStockId() != other.getStockId()) {
      throw new IllegalArgumentException(
          "Stock data is for different stocks. ["
              + this.getStockId() + " vs " + other.getStockId() + "]");
    }
    return this.getTradeDate().compareTo(other.getTradeDate());
  }
}
