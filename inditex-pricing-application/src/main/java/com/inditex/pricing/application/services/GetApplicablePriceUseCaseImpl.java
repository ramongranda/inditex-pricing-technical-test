
package com.inditex.pricing.application.services;

import java.time.Instant;
import java.util.Optional;

import com.inditex.pricing.application.port.in.GetApplicablePriceUseCase;
import com.inditex.pricing.domain.model.Price;
import com.inditex.pricing.domain.port.out.PriceQueryPort;

import im.aop.loggers.Level;
import im.aop.loggers.advice.before.LogBefore;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

/**
 * Service implementation for retrieving the applicable price based on brand ID, product ID, and application date.
 */
@Validated
@Service
@RequiredArgsConstructor
public class GetApplicablePriceUseCaseImpl implements GetApplicablePriceUseCase {

  private final PriceQueryPort priceQueryPort;

  /**
   * {@inheritDoc}
   */
  @Override
  @LogBefore(level = Level.DEBUG)
  public Optional<Price> execute(@NotNull final Integer brandId, @NotNull final Integer productId, @NotNull final Instant applicationDate) {
    return this.priceQueryPort.findApplicable(brandId, productId, applicationDate);
  }
}
