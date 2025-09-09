
package com.inditex.pricing.rest.delegates.mappers;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import org.mapstruct.Mapper;

/**
 * Mapper interface for date conversions.
 */
@Mapper(componentModel = "spring")
public interface DateMapper {

  /**
   * Converts an Instant to an OffsetDateTime in UTC.
   *
   * @param dateTime the Instant to convert
   * @return the corresponding OffsetDateTime in UTC, or null if the input is null
   */
  default OffsetDateTime toOffsetDateTime(Instant dateTime) {
    return Optional.ofNullable(dateTime).filter(t -> !Instant.MAX.equals(t)).map(t -> t.atOffset(ZoneOffset.UTC)).orElse(null);
  }

  /**
   * Converts an OffsetDateTime to an Instant.
   *
   * @param dateTime the OffsetDateTime to convert
   * @return the corresponding Instant, or null if the input is null
   */
  default Instant toInstant(OffsetDateTime dateTime) {
    return Optional.ofNullable(dateTime).map(OffsetDateTime::toInstant).orElse(Instant.MAX);
  }

}
