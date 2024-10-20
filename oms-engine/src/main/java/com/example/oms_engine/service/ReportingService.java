package com.example.oms_engine.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.oms_engine.model.Trade;
import com.example.oms_engine.repository.TradeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReportingService {
    private final TradeRepository tradeRepository;

    public List<Trade> getTradesByProductCode(String productCode) {
        return tradeRepository.findByProductCode(productCode);
    }

    public int getTradedVolumeByProductCode(String productCode) {
        return tradeRepository.sumQuantityByProductCode(productCode);
    }

    public int getTradedVolumeByCurrencyCode(String currencyCode) {
        return tradeRepository.sumQuantityByCurrencyCode(currencyCode);
    }

    public int getTradedVolumeByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return tradeRepository.sumQuantityByDateRange(startDate, endDate);
    }
}
