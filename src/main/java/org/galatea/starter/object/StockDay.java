package org.galatea.starter.object;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.galatea.starter.utils.json.serialization.DateSerializer;

@Data
@Entity
@Builder
@Table(name = "price")
@IdClass(StockSymbolId.class)
@RequiredArgsConstructor
@AllArgsConstructor
public class StockDay implements Serializable, Comparable<StockDay> {

  @Id
  @JsonIgnore
  private int stockId;
  @Column(name = "OpenPrice")
  private BigDecimal open;
  @Column(name = "HighPrice")
  private BigDecimal high;
  @Column(name = "LowPrice")
  private BigDecimal low;
  @Column(name = "ClosePrice")
  private BigDecimal close;
  private long volume;
  @Id
  @JsonSerialize(using = DateSerializer.class)
  @JsonProperty(value = "event_date")
  private OffsetDateTime eventDate;
  @JsonIgnore
  private LocalTime updateTime;

  /**
   * update this stock symbol with values from the given stock symbol.
   *
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
    if (Objects.nonNull(other.getEventDate())) {
      this.eventDate = other.getEventDate();
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
    return this.getEventDate().compareTo(other.getEventDate());
  }
}
