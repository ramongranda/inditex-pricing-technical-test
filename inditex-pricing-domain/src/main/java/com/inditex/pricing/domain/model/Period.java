
package com.inditex.pricing.domain.model;

import java.time.Instant;
import java.util.Objects;

/**
 * Value Object representing a time period with a start and optional end date. Ensures that the start date is not null and that the end
 * date, if provided, is after the start date.
 *
 * @param start the start date of the period, must not be null
 * @param end the end date of the period, can be null but if provided must be after startDate
 */
public record Period(Instant start, Instant end) {

  /**
   * Constructs a Period instance ensuring that startDate is not null, and that endDate, if provided, is after startDate.
   *
   * @param start the start date of the period, must not be null
   * @param end the end date of the period, can be null but if provided must be after startDate
   * @throws NullPointerException if startDate is null
   * @throws IllegalArgumentException if endDate is before startDate
   */
  public Period {
    Objects.requireNonNull(start);
    Objects.requireNonNull(end);
    if (end.isBefore(start)) {
      throw new IllegalArgumentException("end >= start");
    }
  }

}
