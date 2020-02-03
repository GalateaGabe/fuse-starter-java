package org.galatea.starter.utils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.NonNull;
import org.galatea.starter.object.StockSymbol;
import org.galatea.starter.utils.holiday.FederalHolidays;

public class DateTimeUtils {

  private DateTimeUtils() {}

  /**
   * Returns true if the time in question occurred during stock market business hours.
   */
  public static boolean duringBusinessHours(final LocalTime localTime) {
    if (localTime == null) {
      return false;
    }

    final LocalTime exchangeOpen = LocalTime.of(9, 30);
    final LocalTime exchangeClose = LocalTime.of(16, 00);
    return !localTime.isBefore(exchangeOpen) && !localTime.isAfter(exchangeClose);

  }

  /**
   * returns true if the map in question is missing any days between the start and end of the range
   * (inclusive). <br> if a missing day is a weekend or a holiday, then it will be skipped.
   */
  public static boolean missingDays(@NonNull final LocalDate startOfRange,
      @NonNull final LocalDate endOfRange,
      final List<StockSymbol> stockData) {

    //send to map for easy searching
    final Map<LocalDate, StockSymbol> stockDataMap = new HashMap<>();
    stockData.forEach(stock -> stockDataMap.put(stock.getTradeDate().toLocalDate(), stock));

    //<= and ++day because we want the first to be exclusive and the second to be inclusive.
    for (int day = 0; day < ChronoUnit.DAYS.between(startOfRange, endOfRange); day++) {
      final LocalDate date = startOfRange.plusDays(day);
      //if this is a weekday (and not a holiday),
      //and the set does not contain this day, then we have a missing day.
      final List<DayOfWeek> weekend = Arrays.asList(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);
      final List<Integer> years =
          IntStream.rangeClosed(startOfRange.getYear(), endOfRange.getYear())
              .boxed().collect(Collectors.toList());
      final List<LocalDate> holidays = FederalHolidays.byYears(years);
      if (weekend.contains(date.getDayOfWeek())
          || holidays.contains(date)) {
        continue;
      }

      if (!stockDataMap.containsKey(date)) {
        return true;
      }
    }
    return false;
  }
}
