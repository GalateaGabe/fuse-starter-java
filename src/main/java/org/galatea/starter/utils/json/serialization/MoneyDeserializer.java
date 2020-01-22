package org.galatea.starter.utils.json.serialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import java.math.BigDecimal;

public class MoneyDeserializer extends JsonDeserializer<BigDecimal> {

  @Override
  public BigDecimal deserialize(JsonParser jsonParser,
      DeserializationContext deserializationContext) throws IOException {
    return new BigDecimal(jsonParser.getValueAsString());
  }
}
