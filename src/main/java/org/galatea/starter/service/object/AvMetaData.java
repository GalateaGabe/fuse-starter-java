package org.galatea.starter.service.object;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import lombok.Data;
import org.galatea.starter.utils.json.serialization.DateDeserializer;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AvMetaData {
  @JsonProperty("1. Information")
  private String information;
  @JsonProperty("2. Symbol")
  private String symbol;
  @JsonProperty("3. Last Refreshed")
  @JsonDeserialize(using = DateDeserializer.class)
  private OffsetDateTime lastRefresh;
  @JsonProperty("4. Output Size")
  private String outputSize;
  @JsonProperty("5. Time Zone")
  private ZoneId timezone;
}
