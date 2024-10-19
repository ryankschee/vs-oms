package com.example.oms_engine.controller;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.oms_engine.model.Order;
import com.example.oms_engine.service.OrderMatchingService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderMatchingService orderMatchingService;

    @PostMapping("/submit")
    public ResponseEntity<String> submitOrder(@RequestBody Order order) {
        order.setSubmissionTime(LocalDateTime.now());
        orderMatchingService.addOrder(order);
        return ResponseEntity.ok("Order submitted and processed.");
    }
}
