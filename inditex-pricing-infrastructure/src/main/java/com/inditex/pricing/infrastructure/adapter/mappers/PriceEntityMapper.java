
package com.inditex.pricing.infrastructure.adapter.mappers;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

import com.inditex.pricing.domain.model.Money;
import com.inditex.pricing.domain.model.Period;
import com.inditex.pricing.domain.model.Price;
import com.inditex.pricing.infrastructure.jpa.PriceEntity;

import org.apache.commons.lang3.ObjectUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * Mapper interface for converting between PriceEntity and Price domain model.
 */
@Mapper(componentModel = "spring")
public interface PriceEntityMapper {

  /**
   * Maps a PriceEntity to a Price domain model.
   *
   * @param source the PriceEntity to map
   * @return the corresponding Price domain model
   */
  @Mapping(target = "period", source = "source", qualifiedByName = "toPeriod")
  @Mapping(target = "money", source = "source", qualifiedByName = "toMoney")
  Price toModel(PriceEntity source);

  /**
   * Converts a PriceEntity to a Money object.
   *
   * @param source the PriceEntity containing price and currency information
   * @return the corresponding Money object, or null if the entity is null
   */
  @Named("toMoney")
  default Money toMoney(PriceEntity source) {
    return Optional.ofNullable(source)
        .filter(e -> ObjectUtils.allNotNull(e.getPrice(), e.getCurr()))
        .map(e -> new Money(e.getPrice(), e.getCurr()))
        .orElse(null);
  }

  /**
   * Converts a PriceEntity to a Period object.
   *
   * @param source the PriceEntity containing start and end date information
   * @return the corresponding Period object, or null if the entity is null or dates are missing
   */
  @Named("toPeriod")
  default Period toPeriod(PriceEntity source) {
    return Optional.ofNullable(source)
        .map(e -> {
          Instant start = toInstant(e.getStartDate(), null);
          Instant end = toInstant(e.getEndDate(), null);
          return ObjectUtils.allNotNull(start, end) ? new Period(start, end) : null;
        })
        .orElse(null);
  }

  /**
   * Converts a LocalDateTime to an Instant, returning a default value if the source is null.
   *
   * @param source the LocalDateTime to convert
   * @param defaultValue the default Instant to return if source is null
   * @return the corresponding Instant, or the default value if source is null
   */
  static Instant toInstant(LocalDateTime source, Instant defaultValue) {
    return Optional.ofNullable(source).map(t -> t.atZone(ZoneId.of("Europe/Madrid"))).map(ZonedDateTime::toInstant).orElse(defaultValue);
  }
}
