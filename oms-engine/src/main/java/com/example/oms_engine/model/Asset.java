package com.example.oms_engine.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Asset {
    private Long id;
    private String productCode;
    private String productName;
    private String currency;
    private int minOrderQuantity;
}
