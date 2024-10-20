package com.example.oms_engine.controller;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.oms_engine.service.OrderMatchingService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderScheduler {
    private final OrderMatchingService orderMatchingService;
    
    @Scheduled(fixedRate = 3600000) // Check every hour
    public void checkForExpiredOrders() {
        log.info("Checking for expired orders...");
        orderMatchingService.checkForExpiredOrders();
    }
}
