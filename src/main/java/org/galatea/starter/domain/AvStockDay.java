package org.galatea.starter.domain;

import com.fasterxml.jackson.annotation.JsonAlias;
import java.io.Serializable;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class AvStockDay implements Serializable {

  @JsonAlias(value = "1. open")
  private BigDecimal open;
  @JsonAlias(value = "2. high")
  private BigDecimal high;
  @JsonAlias(value = "3. low")
  private BigDecimal low;
  @JsonAlias(value = "4. close")
  private BigDecimal close;
  @JsonAlias(value = "5. volume")
  private long volume;
}
