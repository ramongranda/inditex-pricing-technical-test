
package com.inditex.pricing.application.port.in;

import java.time.Instant;
import java.util.Optional;

import com.inditex.pricing.domain.model.Price;

import jakarta.validation.constraints.NotNull;

/**
 * Use case for retrieving the applicable price based on brand ID, product ID, and application date.
 */
public interface GetApplicablePriceUseCase {

  /**
   * Executes the use case to find the applicable price.
   *
   * @param brandId the brand ID
   * @param productId the product ID
   * @param applicationDate the application date
   * @return an Optional containing the applicable Price if found, otherwise an empty Optional
   */
  Optional<Price> execute(@NotNull Integer brandId, @NotNull final Integer productId, @NotNull final Instant applicationDate);
}
