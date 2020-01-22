package org.galatea.starter.service.object;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
public class StockRequestMetaData {
  private String requestTime;
  private String symbol;
  private List<StockRequestMetaDataMessage> messages = new ArrayList<>();

  public void addMessage(@NonNull String name, @NonNull String body){
    messages.add(new StockRequestMetaDataMessage(name, body));
  }

}
