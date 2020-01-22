package org.galatea.starter.service.object;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.galatea.starter.utils.json.serialization.MessageSerializer;

@Data
@AllArgsConstructor
@JsonSerialize(using = MessageSerializer.class)
public class StockRequestMetaDataMessage {

  @JsonIgnore
  String name;
  String body;
}
