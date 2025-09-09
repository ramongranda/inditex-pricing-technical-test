
package com.inditex.pricing.infrastructure.adapter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

import com.inditex.pricing.domain.model.Price;
import com.inditex.pricing.domain.port.out.PriceQueryPort;
import com.inditex.pricing.infrastructure.adapter.mappers.PriceEntityMapper;
import com.inditex.pricing.infrastructure.jpa.PriceJpaRepository;

import im.aop.loggers.Level;
import im.aop.loggers.advice.before.LogBefore;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

/**
 * JPA adapter implementation for querying prices from the database.
 */
@Validated
@Service
@RequiredArgsConstructor
public class PriceQueryJpaAdapter implements PriceQueryPort {

  private final PriceJpaRepository priceJpaRepository;

  private final PriceEntityMapper priceEntityMapper;

  /**
   * {@inheritDoc}
   */
  @Override
  @LogBefore(level = Level.DEBUG)
  public Optional<Price> findApplicable(@NotNull final Integer brandId, @NotNull final Integer productId, @NotNull final Instant at) {
    return this.priceJpaRepository.findBest(brandId, productId, LocalDateTime.ofInstant(at, ZoneId.of("Europe/Madrid")))
        .map(this.priceEntityMapper::toModel);
  }
}
