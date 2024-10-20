package com.example.oms_engine.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "orders")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String clientCode;
    private String productCode;
    private int quantity;               // Number of units
    private double price;               // Order price
    private String currencyCode;        // Currency code
    private String type;                // Buy or Sell
    private LocalDateTime submissionTime;
    private String status;              // Open, PartiallyFilled, Filled, Cancelled, Expired
}
