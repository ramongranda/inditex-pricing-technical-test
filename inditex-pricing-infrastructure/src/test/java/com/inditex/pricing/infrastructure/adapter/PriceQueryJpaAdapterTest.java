
package com.inditex.pricing.infrastructure.adapter;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

import com.inditex.pricing.domain.model.Price;
import com.inditex.pricing.infrastructure.adapter.mappers.PriceEntityMapperImpl;
import com.inditex.pricing.infrastructure.jpa.PriceEntity;
import com.inditex.pricing.infrastructure.jpa.PriceJpaRepository;

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
    classes = {PriceQueryJpaAdapter.class, PriceEntityMapperImpl.class}
)
class PriceQueryJpaAdapterTest {

  @Autowired
  PriceQueryJpaAdapter priceQueryJpaAdapter;

  @MockitoBean
  PriceJpaRepository priceJpaRepository;

  @Test
  void shouldLoadContext_whenDependenciesAreInjected() {
    // Arrange
    // Act & Assert
    assertNotNull(this.priceQueryJpaAdapter);
  }

  @Test
  void shouldReturnPrice_whenRepositoryReturnsEntity() {
    // Arrange
    Integer brandId = Instancio.create(Integer.class);
    Integer productId = Instancio.create(Integer.class);
    Instant at = Instancio.create(Instant.class);
    LocalDateTime startDate = LocalDateTime.now();
    LocalDateTime endDate = startDate.plusDays(1);
    PriceEntity priceEntity = Instancio.of(PriceEntity.class)
        .set(field("startDate"), startDate)
        .set(field("endDate"), endDate)
        .set(field("curr"), "EUR") // ISO 4217 alpha-3
        .create();
    Mockito.when(priceJpaRepository.findBest(
        brandId,
        productId,
        LocalDateTime.ofInstant(at, ZoneId.of("Europe/Madrid"))
    )).thenReturn(Optional.of(priceEntity));

    // Act
    Optional<Price> result = priceQueryJpaAdapter.findApplicable(brandId, productId, at);

    // Assert
    assertTrue(result.isPresent());
    assertNotNull(result.get());
    verify(priceJpaRepository).findBest(
        brandId,
        productId,
        LocalDateTime.ofInstant(at, ZoneId.of("Europe/Madrid"))
    );
  }

  @Test
  void shouldReturnEmpty_whenRepositoryReturnsEmpty() {
    // Arrange
    Integer brandId = Instancio.create(Integer.class);
    Integer productId = Instancio.create(Integer.class);
    Instant at = Instancio.create(Instant.class);
    Mockito.when(priceJpaRepository.findBest(
        brandId, productId, LocalDateTime.ofInstant(at, ZoneId.of("Europe/Madrid"))
    )).thenReturn(Optional.empty());

    // Act
    Optional<Price> result = priceQueryJpaAdapter.findApplicable(brandId, productId, at);

    // Assert
    assertTrue(result.isEmpty());
    verify(priceJpaRepository).findBest(
        brandId, productId, LocalDateTime.ofInstant(at, ZoneId.of("Europe/Madrid"))
    );
  }
}