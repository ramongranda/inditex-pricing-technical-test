
package com.inditex.pricing.rest.delegates.mappers;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

import com.inditex.pricing.domain.model.Price;
import com.inditex.pricing.rest.dto.PriceDTO;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * Mapper interface for converting between Price and PriceDTO.
 */
@Mapper(uses = {DateMapper.class}, componentModel = "spring")
public interface PriceDTOMapper {

  /**
   * Converts a Price entity to a PriceDTO.
   *
   * @param source the Price entity
   * @return the corresponding PriceDTO
   */
  @Mapping(target = "startDate", source = "period.start")
  @Mapping(target = "price", source = "money.amount", qualifiedByName = "formatAmount")
  @Mapping(target = "endDate", source = "period.end")
  @Mapping(target = "curr", source = "money.currency")
  PriceDTO toDto(Price source);

  /**
   * Formats a BigDecimal amount to two decimal places using HALF_UP rounding.
   *
   * @param amount the BigDecimal amount
   * @return the formatted BigDecimal amount
   */
  @Named("formatAmount")
  default Double formatAmount(BigDecimal amount) {
    return Optional.ofNullable(amount).map(a -> a.setScale(2, RoundingMode.HALF_UP)).map(BigDecimal::doubleValue).orElse(null);
  }
}
