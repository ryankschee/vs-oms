package com.example.oms_engine.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.oms_engine.model.Trade;

public interface TradeRepository extends JpaRepository<Trade, Long> {
    List<Trade> findByProductCode(String productCode);

    @Query("SELECT SUM(t.quantity) FROM Trade t WHERE t.productCode = :productCode")
    int sumQuantityByProductCode(@Param("productCode") String productCode);

    @Query("SELECT SUM(t.quantity) FROM Trade t WHERE t.currencyCode = :currencyCode")
    int sumQuantityByCurrencyCode(@Param("currencyCode") String currencyCode);

    @Query("SELECT SUM(t.quantity) FROM Trade t WHERE t.tradeTime BETWEEN :startDate AND :endDate")
    int sumQuantityByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
