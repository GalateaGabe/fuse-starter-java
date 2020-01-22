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
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;
import org.galatea.starter.service.object.AvResponse;
import org.galatea.starter.utils.json.serialization.DateDeserializer;
import org.galatea.starter.utils.json.serialization.DateSerializer;
import org.galatea.starter.utils.json.serialization.MoneyDeserializer;

@Data
@Entity
@Table(name = "price")
public class StockSymbol implements Serializable, Comparable<StockSymbol> {

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
  private LocalDate tradeDate;
  @JsonIgnore
  private LocalTime updateTime;

  public static List<StockSymbol> updateAll(List<StockSymbol> sourceList,
      int stockId, AvResponse response) {
    Map<LocalDate, StockSymbol> updateMap = response.getSymbols();

    //update any item that exists in both
    sourceList.forEach(stock1 -> {
      StockSymbol refresh = updateMap.get(stock1.getTradeDate());
      if (refresh != null) {
        stock1.update(refresh);
        updateMap.remove(refresh);
      }
    });
    //if there are new items that weren't in the old list, append them to and sort the list.
    if (!updateMap.isEmpty()) {
      updateMap.forEach((date, stock2) -> {
        stock2.setStockId(stockId);
        stock2.setTradeDate(date);
        sourceList.add(stock2);
      });
    }
    return sourceList;

  }


  public void update(StockSymbol other) {
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
    if (Objects.nonNull(other.getVolume())) {
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
  public int compareTo(StockSymbol other) {
    Objects.requireNonNull(other);
    if (this.getStockId() != other.getStockId()) {
      throw new IllegalArgumentException(
          "Stock data is for different stocks. [" +
              this.getStockId() + " vs " + other.getStockId() + "]");
    }
    return this.getTradeDate().compareTo(other.getTradeDate());
  }
}
