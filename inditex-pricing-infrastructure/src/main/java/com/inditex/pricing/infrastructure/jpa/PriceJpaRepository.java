
package com.inditex.pricing.infrastructure.jpa;

import java.time.LocalDateTime;
import java.util.Optional;

import im.aop.loggers.Level;
import im.aop.loggers.advice.before.LogBefore;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * JPA repository interface for managing PriceEntity instances.
 */
public interface PriceJpaRepository extends JpaRepository<PriceEntity, Long> {

  /**
   * Finds the best price entity based on brand ID, product ID, and a specific date and time.
   *
   * @param brandId the brand ID
   * @param productId the product ID
   * @param at the specific date and time
   * @return an Optional containing the best matching PriceEntity, if found
   */
  @LogBefore(level = Level.DEBUG)
  @Query(nativeQuery = true, name = "PriceEntity.findBest")
  Optional<PriceEntity> findBest(@NotNull @Param("brandId") Integer brandId, @NotNull @Param("productId") Integer productId,
      @NotNull @Param("at") LocalDateTime at);
}
