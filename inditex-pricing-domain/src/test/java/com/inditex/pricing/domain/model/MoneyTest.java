
package com.inditex.pricing.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

import org.instancio.Instancio;
import org.junit.jupiter.api.Test;

class MoneyTest {

  @Test
  void shouldCreateMoneyWithValidAmountAndCurrency() {
    BigDecimal amount = Instancio.create(BigDecimal.class);
    String currency = "USD";
    Money money = new Money(amount, currency);
    assertEquals(amount, money.amount());
    assertEquals(currency, money.currency());
  }

  @Test
  void shouldThrowExceptionWhenAmountIsNull() {
    String currency = "EUR";
    assertThrows(NullPointerException.class, () -> new Money(null, currency));
  }

  @Test
  void shouldThrowExceptionWhenCurrencyIsNull() {
    BigDecimal amount = Instancio.create(BigDecimal.class);
    assertThrows(NullPointerException.class, () -> new Money(amount, null));
  }

  @Test
  void shouldThrowExceptionWhenCurrencyIsNotAlpha3() {
    BigDecimal amount = Instancio.create(BigDecimal.class);
    String invalidCurrency = "EURO";
    assertThrows(IllegalArgumentException.class, () -> new Money(amount, invalidCurrency));
  }

  @Test
  void shouldThrowExceptionWhenCurrencyIsTooShort() {
    BigDecimal amount = Instancio.create(BigDecimal.class);
    String invalidCurrency = "EU";
    assertThrows(IllegalArgumentException.class, () -> new Money(amount, invalidCurrency));
  }
}