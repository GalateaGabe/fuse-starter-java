package org.galatea.starter.utils.holiday;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import org.galatea.starter.utils.holiday.FederalHolidays.Observance;
import org.junit.Test;

public class FederalHolidaysTest {

  @Test
  public void dateOf_Holiday() {
    final Observance ny1 = Observance.NEW_YEARS_DAY;
    final LocalDate ny2 = LocalDate.of(2020, 1, 1);

    assertTrue(FederalHolidays.dateOf(ny1, ny2.getYear()).isEqual(ny2));
  }

  @Test
  public void byYear_AllHolidays() {
    final List<LocalDate> expected = Arrays
        .asList(LocalDate.parse("2020-01-01"), LocalDate.parse("2020-01-20"),
            LocalDate.parse("2020-02-17"), LocalDate.parse("2020-05-25"),
            LocalDate.parse("2020-07-03"), LocalDate.parse("2020-09-07"),
            LocalDate.parse("2020-10-12"), LocalDate.parse("2020-11-11"),
            LocalDate.parse("2020-11-26"), LocalDate.parse("2020-12-25"));

    final List<LocalDate> actual = FederalHolidays.byYear(2020);

    assertTrue(expected.containsAll(actual));
    assertTrue(actual.containsAll(expected));

  }

  @Test
  public void byYears_AllHolidays() {
    final List<LocalDate> expected = Arrays
        .asList(LocalDate.parse("2020-01-01"), LocalDate.parse("2020-01-20"),
            LocalDate.parse("2020-02-17"), LocalDate.parse("2020-05-25"),
            LocalDate.parse("2020-07-03"), LocalDate.parse("2020-09-07"),
            LocalDate.parse("2020-10-12"), LocalDate.parse("2020-11-11"),
            LocalDate.parse("2020-11-26"), LocalDate.parse("2020-12-25"),
            LocalDate.parse("2021-01-01"), LocalDate.parse("2021-01-18"),
            LocalDate.parse("2021-02-15"), LocalDate.parse("2021-05-31"),
            LocalDate.parse("2021-07-05"), LocalDate.parse("2021-09-06"),
            LocalDate.parse("2021-10-11"), LocalDate.parse("2021-11-11"),
            LocalDate.parse("2021-11-25"), LocalDate.parse("2021-12-24"));

    final List<LocalDate> actual = FederalHolidays.byYears(Arrays.asList(2020, 2021));

    assertTrue(expected.containsAll(actual));
    assertTrue(actual.containsAll(expected));

  }

}
