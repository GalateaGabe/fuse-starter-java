package org.galatea.starter.service.object;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.galatea.starter.object.StockSymbol;

@Builder
@Data
@AllArgsConstructor
public class StockDataResponse {

  @JsonProperty(value = "meta_data")
  private StockRequestMetaData metaData;
  @JsonProperty(value = "data")
  private List<StockSymbol> symbolList;
}
