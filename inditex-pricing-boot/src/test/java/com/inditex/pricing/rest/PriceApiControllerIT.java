
package com.inditex.pricing.rest;

import static org.hamcrest.Matchers.closeTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class PriceApiControllerIT {

  @Autowired
  MockMvc mockMvc;

  static Stream<Arguments> cases() {
    return Stream.of(
        Arguments.of("2020-06-14T10:00:00Z", 1, 35.50),
        Arguments.of("2020-06-14T16:00:00Z", 2, 25.45),
        Arguments.of("2020-06-14T21:00:00Z", 1, 35.50),
        Arguments.of("2020-06-15T10:00:00Z", 3, 30.50),
        Arguments.of("2020-06-16T21:00:00Z", 4, 38.95)
    );
  }

  @ParameterizedTest
  @MethodSource("cases")
  void should_return_applicable_price(String isoDate, Integer expectedList, Double expectedPrice) throws Exception {

    this.mockMvc.perform(get("/prices")
        .param("applicationDate", isoDate)
        .param("productId", "35455")
        .param("brandId", "1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.priceList").value(expectedList))
        .andExpect(jsonPath("$.price").value(closeTo(expectedPrice, 0.0001)))
        .andExpect(jsonPath("$.curr").value("EUR"));
  }
}
