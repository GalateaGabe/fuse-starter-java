package org.galatea.starter.domain;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.io.Serializable;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.galatea.starter.utils.json.serialization.MoneyDeserializer;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class AvStockDay implements Serializable {

  @JsonDeserialize(using = MoneyDeserializer.class)
  @JsonAlias(value = "1. open")
  private BigDecimal open;
  @JsonDeserialize(using = MoneyDeserializer.class)
  @JsonAlias(value = "2. high")
  private BigDecimal high;
  @JsonDeserialize(using = MoneyDeserializer.class)
  @JsonAlias(value = "3. low")
  private BigDecimal low;
  @JsonDeserialize(using = MoneyDeserializer.class)
  @JsonAlias(value = "4. close")
  private BigDecimal close;
  @JsonAlias(value = "5. volume")
  private long volume;
}
