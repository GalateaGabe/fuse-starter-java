package org.galatea.starter.service;

import org.galatea.starter.service.object.AvResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * A Feign Declarative REST Client to access endpoints the AlphaVantage API for yahoo finance.
 * documentation at https://www.alphavantage.co/documentation/
 */
@FeignClient(name = "AV", url = "${spring.rest.avBasePath}")
public interface AvClient {

  @GetMapping(
      "${spring.rest.avRequestDaily}&symbol={symbol}&apikey=${spring.api.config.avApiKey}&outputsize=full")
  AvResponse getDailyTimeSeries(@PathVariable(name = "symbol") final String symbol);

  @GetMapping(
      "${spring.rest.avRequestDaily}&symbol={symbol}&apikey=${spring.api.config.avApiKey}&outputsize=compact")
  AvResponse getDailyTimeSeriesCompact(@PathVariable(name = "symbol") final String symbol);
}
