package com.example.oms_reporting.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.oms_reporting.model.Trade;
import com.example.oms_reporting.repository.TradeRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/reporting")
@RequiredArgsConstructor
public class ReportingController {
    private final TradeRepository tradeRepository;

    @GetMapping("/trades")
    public ResponseEntity<List<Trade>> getAllTrades() {
        return ResponseEntity.ok(tradeRepository.findAll());
    }

    @GetMapping("/traded-volume")
    public ResponseEntity<Integer> getTradedVolume(@RequestParam String productCode) {
        return ResponseEntity.ok(tradeRepository.sumQuantityByProductCode(productCode));
    }
}
