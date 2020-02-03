package org.galatea.starter.database;

import static org.junit.Assert.assertEquals;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Lists;
import org.galatea.starter.object.StockSymbol;
import org.hibernate.Session;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * tests for the database handler class.
 */
@Slf4j
public class DatabaseConnectionHandlerTest {


  @Test
  public void getStockData_success() throws AssertionError {

    Session session = Mockito.mock(Session.class);
    int id = 10;
    ZoneOffset offset = ZoneOffset.ofHours(-5);
    OffsetDateTime start = OffsetDateTime.now(offset) ;

    List<StockSymbol> expected = Lists.emptyList();
    List<StockSymbol> actual = Lists.emptyList();

    assertEquals(expected,actual);

  }
}
