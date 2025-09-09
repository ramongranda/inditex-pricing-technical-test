
package com.inditex.pricing.domain.port.out;

import java.time.Instant;
import java.util.Optional;

import com.inditex.pricing.domain.model.Price;

import jakarta.validation.constraints.NotNull;

/**
 * Repository interface for accessing Price data.
 */
public interface PriceQueryPort {

  /**
   * Finds prices by brand ID, product ID, and application date.
   *
   * @param brandId the brand ID
   * @param productId the product ID
   * @param at the application date
   * @return a list of Price entities matching the criteria
   */
  Optional<Price> findApplicable(@NotNull Integer brandId, @NotNull Integer productId, @NotNull Instant at);
}
