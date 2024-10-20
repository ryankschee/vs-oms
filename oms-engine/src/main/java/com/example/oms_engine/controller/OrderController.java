package com.example.oms_engine.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.oms_engine.model.Order;
import com.example.oms_engine.service.OrderMatchingService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {
    private final OrderMatchingService orderMatchingService;

    @PostMapping("/submission")
    public ResponseEntity<String> submitOrder(@RequestBody Order order) {
        order.setSubmissionTime(LocalDateTime.now());
        log.info("Received order: {}", order);

        orderMatchingService.addOrder(order);
        return ResponseEntity.ok("Order submitted and processed.");
    }

    @PostMapping("/bulk-submission")
    public ResponseEntity<String> submitBulkOrders(@RequestBody List<Order> orders) {
        for (Order order : orders) {
            order.setSubmissionTime(LocalDateTime.now());
            log.info("Received order: {}", order);
            orderMatchingService.addOrder(order);
        }
        return ResponseEntity.ok("Bulk orders submitted and processed.");
    }
}
