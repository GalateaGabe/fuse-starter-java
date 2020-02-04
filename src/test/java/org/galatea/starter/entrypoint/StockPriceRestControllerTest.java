package org.galatea.starter.entrypoint;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import junitparams.JUnitParamsRunner;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.galatea.starter.ASpringTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@RequiredArgsConstructor
@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureWireMock(port = 0, files = "classpath:/wiremock")
@RunWith(JUnitParamsRunner.class)
public class StockPriceRestControllerTest extends ASpringTest {

  @Autowired
  private MockMvc mvc;


  @Test
  public void getRecentStockPrice_compact() throws Exception {

    MvcResult result = mvc.perform(MockMvcRequestBuilders.get("/prices?stock=MSFT&days=1")
        .accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
        .andExpect(jsonPath("meta_data.symbol", is(equalTo("MSFT"))))
        .andExpect(jsonPath("data[0].open", is(equalTo(85.9400))))
        .andExpect(jsonPath("data[0].high", is(equalTo(86.9200))))
        .andExpect(jsonPath("data[0].low", is(equalTo(85.2400))))
        .andExpect(jsonPath("data[0].close", is(equalTo(85.7000))))
        .andExpect(jsonPath("data[0].volume", is(equalTo(1014640))))
        .andExpect(jsonPath("data[0].trade_date", is(equalTo("2020-02-03"))))
        .andReturn();
  }
  @Test
  public void getRecentStockPrice_badDate() throws Exception {

    MvcResult result = mvc.perform(MockMvcRequestBuilders.get("/prices?stock=MSFT&days=-1")
        .accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
        .andExpect(jsonPath("meta_data.messages.error", is(equalTo("days must be a positive integer."))))
        .andExpect(jsonPath("data", empty()))
        .andReturn();
  }
}
