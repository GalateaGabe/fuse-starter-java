package org.galatea.starter.utils;

import java.time.LocalTime;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class DateTimeUtilsTest {

  @Test
  public void duringBusinessHours_True() {
    final LocalTime time = LocalTime.of(11, 0);

    assertTrue(DateTimeUtils.duringBusinessHours(time));

  }

  @Test
  public void duringBusinessHours_False() {
    final LocalTime time = LocalTime.of(0, 0);

    assertFalse(DateTimeUtils.duringBusinessHours(time));
  }
}
