
package com.inditex.pricing.rest.delegates;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.OffsetDateTime;
import java.util.Optional;

import com.inditex.pricing.application.port.in.GetApplicablePriceUseCase;
import com.inditex.pricing.domain.model.Price;
import com.inditex.pricing.rest.api.PricesApiDelegate;
import com.inditex.pricing.rest.delegates.mappers.DateMapperImpl;
import com.inditex.pricing.rest.delegates.mappers.PriceDTOMapperImpl;
import com.inditex.pricing.rest.dto.PriceDTO;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith({MockitoExtension.class, InstancioExtension.class})
@SpringBootTest(
    classes = {PricesApiDelegateImpl.class, PriceDTOMapperImpl.class, DateMapperImpl.class}
)
class PricesApiDelegateImplTest {

  @Autowired
  PricesApiDelegate pricesApiDelegate;

  @MockitoBean
  GetApplicablePriceUseCase getApplicablePriceUseCase;

  @Test
  void shouldLoadContext_whenDependenciesAreInjected() {
    // Arrange
    // Act & Assert
    assertNotNull(this.pricesApiDelegate);
  }

  @Test
  void shouldReturnPriceDTO_whenApplicablePriceExists() {
    // Arrange
    OffsetDateTime applicationDate = Instancio.create(OffsetDateTime.class);
    Integer productId = Instancio.create(Integer.class);
    Integer brandId = Instancio.create(Integer.class);
    Price price = Instancio.of(Price.class)
        .set(field(Price::getProductId), productId)
        .set(field(Price::getBrandId), brandId)
        .create();
    when(this.getApplicablePriceUseCase.execute(brandId, productId, applicationDate.toInstant())).thenReturn(Optional.of(price));
    // Act
    PriceDTO result = pricesApiDelegate.getPrices(applicationDate, productId, brandId);
    // Assert
    assertNotNull(result);
    assertEquals(productId, result.getProductId());
    assertEquals(brandId, result.getBrandId());
    verify(this.getApplicablePriceUseCase).execute(brandId, productId, applicationDate.toInstant());
  }

  @Test
  void shouldThrowNotFound_whenNoApplicablePriceExists() {
    // Arrange
    OffsetDateTime applicationDate = Instancio.create(OffsetDateTime.class);
    Integer productId = Instancio.create(Integer.class);
    Integer brandId = Instancio.create(Integer.class);
    when(getApplicablePriceUseCase.execute(brandId, productId, applicationDate.toInstant()))
        .thenReturn(Optional.empty());
    // Act & Assert
    assertThrows(ResponseStatusException.class, () -> pricesApiDelegate.getPrices(applicationDate, productId, brandId));
    verify(this.getApplicablePriceUseCase).execute(brandId, productId, applicationDate.toInstant());
  }
}