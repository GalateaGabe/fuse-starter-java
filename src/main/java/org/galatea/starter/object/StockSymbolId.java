package org.galatea.starter.object;

import java.io.Serializable;
import java.time.OffsetDateTime;
import lombok.Data;

@Data
public class StockSymbolId implements Serializable {
  private int stockId;
  private OffsetDateTime tradeDate;
}
