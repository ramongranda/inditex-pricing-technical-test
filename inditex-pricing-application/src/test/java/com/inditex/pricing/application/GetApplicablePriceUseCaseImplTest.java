
package com.inditex.pricing.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.Optional;

import com.inditex.pricing.application.port.in.GetApplicablePriceUseCase;
import com.inditex.pricing.application.services.GetApplicablePriceUseCaseImpl;
import com.inditex.pricing.domain.model.Price;
import com.inditex.pricing.domain.port.out.PriceQueryPort;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@ExtendWith({MockitoExtension.class, InstancioExtension.class})
@SpringBootTest(
    classes = {GetApplicablePriceUseCaseImpl.class}
)
class GetApplicablePriceUseCaseImplTest {

  @Autowired
  GetApplicablePriceUseCase getApplicablePriceUseCase;

  @MockitoBean
  PriceQueryPort priceQueryPort;

  @Test
  void shouldLoadContext_whenDependenciesAreInjected() {
    // Arrange
    // Act & Assert
    assertNotNull(this.getApplicablePriceUseCase);
  }

  @Test
  void shouldReturnPrice_whenPortReturnsPrice() {
    // Arrange
    Integer brandId = Instancio.create(Integer.class);
    Integer productId = Instancio.create(Integer.class);
    Instant applicationDate = Instancio.create(Instant.class);
    Price price = Instancio.of(Price.class).create();
    Mockito.when(priceQueryPort.findApplicable(brandId, productId, applicationDate)).thenReturn(Optional.of(price));

    // Act
    Optional<Price> result = getApplicablePriceUseCase.execute(brandId, productId, applicationDate);

    // Assert
    assertNotNull(result);
    assertTrue(result.isPresent());
    assertEquals(price, result.get());
  }

  @Test
  void shouldReturnEmpty_whenPortReturnsEmpty() {
    // Arrange
    Integer brandId = Instancio.create(Integer.class);
    Integer productId = Instancio.create(Integer.class);
    Instant applicationDate = Instancio.create(Instant.class);
    Mockito.when(priceQueryPort.findApplicable(brandId, productId, applicationDate)).thenReturn(Optional.empty());

    // Act
    Optional<Price> result = getApplicablePriceUseCase.execute(brandId, productId, applicationDate);

    // Assert
    assertNotNull(result);
    assertTrue(result.isEmpty());
  }
}