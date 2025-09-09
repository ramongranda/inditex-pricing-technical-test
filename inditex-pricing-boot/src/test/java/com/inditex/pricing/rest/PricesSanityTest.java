
package com.inditex.pricing.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;

import com.inditex.pricing.infrastructure.jpa.PriceJpaRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class PricesSanityTest {

  @Autowired
  JdbcTemplate jdbc;

  @Test
  void dump_rows_for_brand1_product35455() {

    jdbc.query("""
        SELECT PRICE_LIST, BRAND_ID, PRODUCT_ID, PRIORITY, START_DATE, END_DATE, PRICE
        FROM PRICES
        WHERE BRAND_ID=1 AND PRODUCT_ID=35455
        ORDER BY START_DATE
        """,
        (ResultSetExtractor<Void>) rs -> {
          while (rs.next()) {
            System.out.printf("PL=%d brand=%d prod=%d prio=%d %s..%s price=%s%n",
                rs.getInt("PRICE_LIST"),
                rs.getInt("BRAND_ID"),
                rs.getInt("PRODUCT_ID"),
                rs.getInt("PRIORITY"),
                rs.getTimestamp("START_DATE"),
                rs.getTimestamp("END_DATE"),
                rs.getBigDecimal("PRICE"));
          }
          return null;
        }
    );

  }

  @Autowired
  PriceJpaRepository repo;

  @Test
  void repository_should_pick_list2_at_18_00_madrid() {
    var atDb = LocalDateTime.of(2020, 6, 14, 18, 0); // 16:00Z -> 18:00 Europe/Madrid
    var row = repo.findBest(1, 35455, atDb).orElseThrow();
    assertEquals(2, row.getPriceList(), "Debe elegir la tarifa 2 a las 18:00 Madrid");
  }
}
