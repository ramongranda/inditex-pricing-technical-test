
package com.inditex.pricing.infrastructure.jpa;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * JPA entity representing a price record in the database.
 */
@Entity
@Table(name = "PRICES")
@Data
@EqualsAndHashCode
public class PriceEntity implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue
  private Long id;

  private Integer brandId;

  private Integer productId;

  private Integer priceList;

  private Integer priority;

  @Column(name = "PRICE", precision = 10, scale = 2)
  private BigDecimal price;

  private String curr;

  private LocalDateTime startDate;

  private LocalDateTime endDate;
}
