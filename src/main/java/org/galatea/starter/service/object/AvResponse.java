package org.galatea.starter.service.object;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.util.Map;
import lombok.Builder;
import lombok.Data;
import org.galatea.starter.object.StockDay;

@Builder
@Data
public class AvResponse {

  @JsonProperty("Meta Data")
  private AvMetaData metaData;
  @JsonProperty(value = "Time Series (Daily)", access = JsonProperty.Access.WRITE_ONLY)
  private Map<LocalDate, StockDay> symbols;

}
