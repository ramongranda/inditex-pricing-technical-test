
package com.inditex.pricing.rest.delegates;

import java.time.OffsetDateTime;
import java.util.Optional;

import com.inditex.pricing.application.port.in.GetApplicablePriceUseCase;
import com.inditex.pricing.domain.model.Price;
import com.inditex.pricing.rest.api.PricesApiDelegate;
import com.inditex.pricing.rest.delegates.mappers.DateMapper;
import com.inditex.pricing.rest.delegates.mappers.PriceDTOMapper;
import com.inditex.pricing.rest.dto.PriceDTO;

import im.aop.loggers.Level;
import im.aop.loggers.advice.before.LogBefore;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ResponseStatusException;

/**
 * Implementation of the Prices API delegate.
 */
@Validated
@Service
@RequiredArgsConstructor
public class PricesApiDelegateImpl implements PricesApiDelegate {

  private final GetApplicablePriceUseCase getApplicablePriceUseCase;

  private final PriceDTOMapper priceDTOMapper;

  private final DateMapper dateMapper;

  /**
   * {@inheritDoc}
   */
  @LogBefore(level = Level.INFO)
  @Override
  public PriceDTO getPrices(final OffsetDateTime applicationDate, final Integer productId, final Integer brandId) {
    Optional<Price> price = this.getApplicablePriceUseCase.execute(brandId, productId, dateMapper.toInstant(applicationDate));
    return price.map(this.priceDTOMapper::toDto)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No applicable price found"));
  }
}
