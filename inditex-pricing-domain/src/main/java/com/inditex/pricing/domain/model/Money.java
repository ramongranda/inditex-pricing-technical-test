
package com.inditex.pricing.domain.model;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Value Object representing a monetary amount with its associated currency. Ensures that the amount is not null and that the currency
 * adheres to the ISO 4217 alpha-3 standard.
 *
 * @param amount the monetary amount, must not be null
 * @param currency the currency code in ISO 4217 alpha-3 format, must not be null and must be exactly 3 characters long
 */
public record Money(BigDecimal amount, String currency) {

  /**
   * Constructs a Money instance ensuring that amount and currency are not null, and that currency adheres to the ISO 4217 alpha-3
   * standard.
   *
   * @param amount the monetary amount, must not be null
   * @param currency the currency code in ISO 4217 alpha-3 format, must not be null and must be exactly 3 characters long
   * @throws NullPointerException if amount or currency is null
   * @throws IllegalArgumentException if currency is not exactly 3 characters long
   */
  public Money {
    Objects.requireNonNull(amount, "amount must not be null");
    Objects.requireNonNull(currency, "currency must not be null");
    if (currency.length() != 3) {
      throw new IllegalArgumentException("currency must be ISO 4217 alpha-3");
    }
  }
}
