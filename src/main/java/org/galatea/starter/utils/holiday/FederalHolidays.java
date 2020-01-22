package org.galatea.starter.utils.holiday;

/**
 * Modified from https://gist.github.com/bdkosher/9414748 to support java8 zdt instead of calendar
 */

import static java.time.DayOfWeek.MONDAY;
import static java.time.DayOfWeek.SATURDAY;
import static java.time.DayOfWeek.SUNDAY;
import static java.time.DayOfWeek.THURSDAY;
import static java.time.Month.DECEMBER;
import static java.time.Month.FEBRUARY;
import static java.time.Month.JANUARY;
import static java.time.Month.JULY;
import static java.time.Month.MAY;
import static java.time.Month.NOVEMBER;
import static java.time.Month.OCTOBER;
import static java.time.Month.SEPTEMBER;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * For a holiday that falls on Saturday, the Federal holiday will be observed the previous Friday.
 * If it falls on Sunday, it will be observed the following Monday.
 */
public class FederalHolidays {

  /**
   * The list of Federal Observances, as per section 6103(a) of title 5 of the United States Code.
   *
   * see http://www.law.cornell.edu/uscode/text/5/6103
   */
  public enum Observance {

    /**
     * January 1st.
     */
    NEW_YEARS_DAY(JANUARY, 1),
    /**
     * Third Monday in January.
     */
    BIRTHDAY_OF_MARTIN_LUTHER_KING_JR(JANUARY, MONDAY, 3),
    /**
     * Third Monday in February.
     */
    WASHINGTONS_BIRTHDAY(FEBRUARY, MONDAY, 3),
    /**
     * Last Monday in May.
     */
    MEMORIAL_DAY(MAY, MONDAY, -1),
    /**
     * July 4th.
     */
    INDEPENDENCE_DAY(JULY, 4),
    /**
     * First Monday in September.
     */
    LABOR_DAY(SEPTEMBER, MONDAY, 1),
    /**
     * Second Monday in October.
     */
    COLUMBUS_DAY(OCTOBER, MONDAY, 2),
    /**
     * November 11th.
     */
    VETERANS_DAY(NOVEMBER, 11),
    /**
     * Fourth Thursday in November.
     */
    THANKSGIVING_DAY(NOVEMBER, THURSDAY, 4),
    /**
     * December 25th.
     */
    CHIRSTMAS_DAY(DECEMBER, 25);

    private final Month month;
    private final int dayOfMonth;
    private final DayOfWeek dayOfWeek;
    private final int weekOfMonth;
    private static final int NA = 0;

    private Observance(Month month, int dayOfMonth) {
      this.month = month;
      this.dayOfMonth = dayOfMonth;
      this.dayOfWeek = null;
      this.weekOfMonth = NA;
    }

    private Observance(Month month, DayOfWeek dayOfWeek, int weekOfMonth) {
      this.month = month;
      this.dayOfMonth = NA;
      this.dayOfWeek = dayOfWeek;
      this.weekOfMonth = weekOfMonth;
    }

    /**
     * Returns true if this observance is a fixed date, e.g. December 25th or January 1st. Non-fixed
     * dates are those that are on a particular day of week and week of the month, e.g. 3rd Thursday
     * in November.
     */
    boolean isFixedDate() {
      return dayOfMonth != NA;
    }
  }

  /**
   * Note, it is possible for the New Year's Day observance to be in the year previous to the one
   * provided. For example, New Years 2011 was observed on December 31st, 2010.
   */
  public static LocalDate dateOf(Observance observance, int year) {
    LocalDate ld;
    YearMonth ym = YearMonth.of(year, observance.month);
    if (observance.isFixedDate()) {
      ld = ym.atDay(observance.dayOfMonth);
    } else {
      Objects.requireNonNull(observance.dayOfWeek);
      //default to first day of the month, (then adjust if positive)
      if (observance.weekOfMonth > 0) {
        ld = ym.atDay(1);
        //set the correct day of week
        ld = ld.with(TemporalAdjusters.nextOrSame(observance.dayOfWeek));
        //then set the correct month by adding in X weeks
        ld = ld.plusDays(7L * (observance.weekOfMonth - 1));
      } else {
        //same, but backwards
        ld = ym.atEndOfMonth();
        ld = ld.with(TemporalAdjusters.previousOrSame(observance.dayOfWeek));
        ld = ld.plusDays(7L * (observance.weekOfMonth + 1));
      }
    }

    ld = adjustForWeekendsIfNecessary(ld);
    return ld;
  }

  /**
   * If the calendar is on a Saturday, adjust the date back a day. If on Sunday, move it forward one
   * day. If neither, leave the date unchanged. See Executive order 11582, February 11, 1971.
   *
   * @param ld - mutated if a weekend date
   * @return
   */
  private static LocalDate adjustForWeekendsIfNecessary(LocalDate ld) {
    DayOfWeek dayOfWeek = ld.getDayOfWeek();
    if (dayOfWeek == SATURDAY) {
      ld = ld.minusDays(1);
    } else if (dayOfWeek == SUNDAY) {
      ld = ld.plusDays(1);
    }
    return ld;
  }

  /**
   * Returns a list of all holidays present in a given year.
   */
  public static List<LocalDate> byYear(final int year) {
    List<LocalDate> results = new ArrayList<>();
    for (Observance holiday : Observance.values()) {
      results.add(dateOf(holiday, year));
    }
    return results;
  }

  /**
   * Returns a list of all holidays present in given years.
   */
  public static List<LocalDate> byYears(final List<Integer> years) {
    List<LocalDate> results = new ArrayList<>();
    for (int year : years) {
      for (Observance holiday : Observance.values()) {
        results.add(dateOf(holiday, year));
      }
    }
    return results;
  }

}
