
package com.inditex.pricing.infrastructure.adapter.mappers;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.inditex.pricing.domain.model.Money;
import com.inditex.pricing.domain.model.Period;
import com.inditex.pricing.domain.model.Price;
import com.inditex.pricing.infrastructure.jpa.PriceEntity;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith({MockitoExtension.class, InstancioExtension.class})
class PriceEntityMapperTest {

  PriceEntityMapper priceEntityMapper;

  @BeforeEach
  void setUp() {
    priceEntityMapper = new PriceEntityMapperImpl();
  }

  @Test
  void toMoney_shouldReturnMoney_whenEntityIsValid() {
    PriceEntity entity = Instancio.of(PriceEntity.class)
        .set(field("price"), BigDecimal.valueOf(99.99))
        .set(field("curr"), "EUR")
        .create();
    Money money = priceEntityMapper.toMoney(entity);
    assertNotNull(money);
    assertEquals(BigDecimal.valueOf(99.99), money.amount());
    assertEquals("EUR", money.currency());
  }

  @Test
  void toMoney_shouldReturnNull_whenEntityIsNull() {
    assertNull(priceEntityMapper.toMoney(null));
  }

  @Test
  void toPeriod_shouldReturnPeriod_whenEntityIsValid() {
    LocalDateTime start = LocalDateTime.now();
    LocalDateTime end = start.plusDays(1);
    PriceEntity entity = Instancio.of(PriceEntity.class)
        .set(field(PriceEntity::getStartDate), start)
        .set(field(PriceEntity::getEndDate), end)
        .create();
    Period period = priceEntityMapper.toPeriod(entity);
    assertNotNull(period);
    assertEquals(start.atZone(java.time.ZoneId.of("Europe/Madrid")).toInstant(), period.start());
    assertEquals(end.atZone(java.time.ZoneId.of("Europe/Madrid")).toInstant(), period.end());
  }

  @Test
  void toPeriod_shouldReturnNull_whenEntityIsNull() {
    assertNull(priceEntityMapper.toPeriod(null));
  }

  @Test
  void toPeriod_shouldReturnNull_whenStartDateIsNull() {
    PriceEntity entity = Instancio.of(PriceEntity.class)
        .set(field(PriceEntity::getStartDate), null)
        .set(field(PriceEntity::getEndDate), LocalDateTime.now())
        .create();
    assertNull(priceEntityMapper.toPeriod(entity));
  }

  @Test
  void toPeriod_shouldReturnNull_whenEndDateIsNull() {
    PriceEntity entity = Instancio.of(PriceEntity.class)
        .set(field(PriceEntity::getStartDate), LocalDateTime.now())
        .set(field(PriceEntity::getEndDate), null)
        .create();
    assertNull(priceEntityMapper.toPeriod(entity));
  }

  @Test
  void toPeriod_shouldReturnNull_whenBothDatesAreNull() {
    PriceEntity entity = Instancio.of(PriceEntity.class)
        .set(field(PriceEntity::getStartDate), null)
        .set(field(PriceEntity::getEndDate), null)
        .create();
    assertNull(priceEntityMapper.toPeriod(entity));
  }

  @Test
  void toPeriod_shouldReturnPeriod_whenBothDatesArePresent() {
    LocalDateTime start = LocalDateTime.now();
    LocalDateTime end = start.plusDays(1);
    PriceEntity entity = Instancio.of(PriceEntity.class)
        .set(field(PriceEntity::getStartDate), start)
        .set(field(PriceEntity::getEndDate), end)
        .create();
    Period period = priceEntityMapper.toPeriod(entity);
    assertNotNull(period);
    assertEquals(start.atZone(java.time.ZoneId.of("Europe/Madrid")).toInstant(), period.start());
    assertEquals(end.atZone(java.time.ZoneId.of("Europe/Madrid")).toInstant(), period.end());
  }

  @Test
  void toModel_shouldMapAllFields_whenEntityIsValid() {
    PriceEntity entity = Instancio.of(PriceEntity.class)
        .set(field(PriceEntity::getPrice), BigDecimal.valueOf(123.45))
        .set(field(PriceEntity::getCurr), "EUR")
        .set(field(PriceEntity::getStartDate), LocalDateTime.now())
        .set(field(PriceEntity::getEndDate), LocalDateTime.now().plusDays(1))
        .create();
    Price price = priceEntityMapper.toModel(entity);
    assertNotNull(price);
    assertNotNull(price.getMoney());
    assertNotNull(price.getPeriod());
    assertEquals(BigDecimal.valueOf(123.45), price.getMoney().amount());
    assertEquals("EUR", price.getMoney().currency());
  }

  @Test
  void toModel_shouldReturnNullFields_whenEntityFieldsAreNull() {
    PriceEntity entity = new PriceEntity();
    Price price = priceEntityMapper.toModel(entity);
    assertNotNull(price);
    assertNull(price.getMoney());
    assertNull(price.getPeriod());
  }

  @Test
  void toModel_shouldReturnNull_whenEntityIsNull() {
    assertNull(priceEntityMapper.toModel(null));
  }
}