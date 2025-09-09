
package com.inditex.pricing.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Instant;

import org.instancio.Instancio;
import org.junit.jupiter.api.Test;

class PeriodTest {

  @Test
  void shouldCreatePeriodWithValidDates() {
    Instant start = Instancio.create(Instant.class);
    Instant end = start.plusSeconds(3600);
    Period period = new Period(start, end);
    assertEquals(start, period.start());
    assertEquals(end, period.end());
  }

  @Test
  void shouldThrowExceptionWhenEndIsBeforeStart() {
    Instant start = Instancio.create(Instant.class);
    Instant end = start.minusSeconds(3600);
    assertThrows(IllegalArgumentException.class, () -> new Period(start, end));
  }

  @Test
  void shouldThrowExceptionWhenStartIsNull() {
    Instant end = Instancio.create(Instant.class);
    assertThrows(NullPointerException.class, () -> new Period(null, end));
  }

  @Test
  void shouldThrowExceptionWhenEndIsNull() {
    Instant start = Instancio.create(Instant.class);
    assertThrows(NullPointerException.class, () -> new Period(start, null));
  }
}