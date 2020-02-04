package org.galatea.starter.utils;

import java.time.LocalTime;
import org.junit.Test;

public class DateTimeUtilsTest {

  @Test
  public void duringBusinessHours_True() {
    final LocalTime time = LocalTime.of(11, 0);

    assert DateTimeUtils.duringBusinessHours(time);

  }

  @Test
  public void duringBusinessHours_False() {
    final LocalTime time = LocalTime.of(0, 0);

    assert !DateTimeUtils.duringBusinessHours(time);
  }
}
