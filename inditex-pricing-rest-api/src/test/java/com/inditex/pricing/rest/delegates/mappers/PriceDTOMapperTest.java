
package com.inditex.pricing.rest.delegates.mappers;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

import org.instancio.junit.InstancioExtension;
import org.instancio.junit.InstancioSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith({MockitoExtension.class, InstancioExtension.class})
class PriceDTOMapperTest {

  PriceDTOMapper priceDTOMapper;

  @BeforeEach
  void setUp() {
    priceDTOMapper = new PriceDTOMapperImpl();
  }

  @Test
  void formatAmount_shouldReturnDoubleWithTwoDecimals_whenAmountIsValid() {
    BigDecimal amount = new BigDecimal("123.4567");
    Double result = priceDTOMapper.formatAmount(amount);
    assertNotNull(result);
    assertEquals(123.46, result, 0.0001);
  }

  @Test
  void formatAmount_shouldReturnNull_whenAmountIsNull() {
    Double result = priceDTOMapper.formatAmount(null);
    assertNull(result);
  }

  @Test
  void formatAmount_shouldReturnZero_whenAmountIsZero() {
    BigDecimal amount = BigDecimal.ZERO;
    Double result = priceDTOMapper.formatAmount(amount);
    assertNotNull(result);
    assertEquals(0.00, result, 0.0001);
  }

  @Test
  void formatAmount_shouldReturnNegative_whenAmountIsNegative() {
    BigDecimal amount = new BigDecimal("-99.999");
    Double result = priceDTOMapper.formatAmount(amount);
    assertNotNull(result);
    assertEquals(-100.00, result, 0.0001);
  }

  @ParameterizedTest
  @InstancioSource(samples = 10)
  void formatAmount_shouldReturnCorrectValue_forRandomAmounts(BigDecimal amount) {
    Double expected = Optional.ofNullable(amount)
        .map(a -> a.setScale(2, RoundingMode.HALF_UP))
        .map(BigDecimal::doubleValue)
        .orElse(null);
    Double result = priceDTOMapper.formatAmount(amount);
    assertEquals(expected, result);
  }
}