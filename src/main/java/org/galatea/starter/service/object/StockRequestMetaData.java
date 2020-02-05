package org.galatea.starter.service.object;

import java.util.HashMap;
import java.util.Map;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
public class StockRequestMetaData {

  private String requestTime;
  private String symbol;
  private Map<String, String> messages = new HashMap<>();

  /**
   * add a single message to the metadata messages map.
   */
  public void addMessage(@NonNull final String name, @NonNull final Object body) {
    messages.put(name, body.toString());
  }

  /**
   * add n strings as a single message to the metadata messages map.
   * @param name name / label of message
   * @param body body contents of message
   */
  public void addMessage(@NonNull final String name, @NonNull final Object... body) {
    final StringBuilder sb = new StringBuilder();
    for (final Object part : body) {
      sb.append(part.toString());
    }
    addMessage(name, sb.toString());
  }

}
