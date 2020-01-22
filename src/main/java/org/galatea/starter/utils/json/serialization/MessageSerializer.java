package org.galatea.starter.utils.json.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import org.galatea.starter.service.object.StockRequestMetaDataMessage;

public class MessageSerializer extends JsonSerializer<StockRequestMetaDataMessage> {

  @Override
  public void serialize(StockRequestMetaDataMessage message, JsonGenerator jsonGenerator,
      SerializerProvider serializerProvider) throws IOException {
    jsonGenerator.writeStartObject();
    jsonGenerator.writeObjectField(message.getName(), message.getBody());
    jsonGenerator.writeEndObject();
  }
}
