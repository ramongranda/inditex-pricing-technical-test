
package com.inditex.pricing.rest.delegates.mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.Instant;
import java.time.OffsetDateTime;

import org.instancio.junit.InstancioExtension;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith({MockitoExtension.class, InstancioExtension.class})
class DateMapperTest {

  static DateMapper dateMapper;

  @BeforeAll
  static void setup() {
    dateMapper = new DateMapperImpl();
  }

  @Test
  void shouldConvertInstantToOffsetDateTime_whenValidInstantIsProvided() {
    // Arrange
    var instant = Instant.parse("2023-10-01T10:15:30.00Z");

    // Act
    var offsetDateTime = dateMapper.toOffsetDateTime(instant);

    // Assert
    assertNotNull(offsetDateTime);
    assertEquals(OffsetDateTime.parse("2023-10-01T10:15:30Z"), offsetDateTime);
  }

  @Test
  void shouldReturnNull_whenNullInstantIsProvided() {
    // Arrange
    // Act
    var offsetDateTime = dateMapper.toOffsetDateTime(null);

    // Assert
    assertNull(offsetDateTime);
  }

  @Test
  void shouldReturnNull_whenInstantIsInstantMax() {
    // Arrange
    var instant = Instant.MAX;

    // Act
    var offsetDateTime = dateMapper.toOffsetDateTime(instant);

    // Assert
    assertNull(offsetDateTime);
  }

  @Test
  void shouldConvertOffsetDateTimeToInstant_whenValidOffsetDateTimeIsProvided() {
    // Arrange
    var offsetDateTime = OffsetDateTime.parse("2023-10-01T10:15:30Z");

    // Act
    var instant = dateMapper.toInstant(offsetDateTime);

    // Assert
    assertNotNull(instant);
    assertEquals(Instant.parse("2023-10-01T10:15:30.00Z"), instant);
  }

}