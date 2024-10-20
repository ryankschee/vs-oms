package com.example.oms_engine.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.oms_engine.model.Trade;
import com.example.oms_engine.service.ReportingService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/reporting")
@RequiredArgsConstructor
public class ReportingController {
    private final ReportingService reportingService;

    @GetMapping("/trades")
    public ResponseEntity<List<Trade>> getTradesByProductCode(@RequestParam String productCode) {
        return ResponseEntity.ok(reportingService.getTradesByProductCode(productCode));
    }

    @GetMapping("/traded-volume/product")
    public ResponseEntity<Integer> getTradedVolumeByProductCode(@RequestParam String productCode) {
        return ResponseEntity.ok(reportingService.getTradedVolumeByProductCode(productCode));
    }

    @GetMapping("/traded-volume/currency")
    public ResponseEntity<Integer> getTradedVolumeByCurrencyCode(@RequestParam String currencyCode) {
        return ResponseEntity.ok(reportingService.getTradedVolumeByCurrencyCode(currencyCode));
    }

    @GetMapping("/traded-volume/date-range")
    public ResponseEntity<Integer> getTradedVolumeByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(reportingService.getTradedVolumeByDateRange(startDate, endDate));
    }
}
