package com.example.oms_reporting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.oms_reporting.model.Trade;

public interface TradeRepository extends JpaRepository<Trade, Long> {
    @Query("SELECT SUM(t.quantity) FROM Trade t WHERE t.productCode = :productCode")
    int sumQuantityByProductCode(@Param("productCode") String productCode);
}
