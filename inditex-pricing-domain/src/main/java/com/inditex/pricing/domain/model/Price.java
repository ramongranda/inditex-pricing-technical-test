
package com.inditex.pricing.domain.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Price entity representing pricing information.
 */
@Data
@EqualsAndHashCode
public class Price {

  @NotNull
  private Long id;

  @NotNull
  private Integer productId;

  @NotNull
  private Integer brandId;

  @NotNull
  private Short priceList;

  @NotNull
  private Period period;

  @NotNull
  private Money money;
}
