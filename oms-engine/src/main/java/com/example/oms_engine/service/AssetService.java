package com.example.oms_engine.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.oms_engine.model.Asset;

@Service
public class AssetService {
    private final RestTemplate restTemplate;
    private final String baseUrl = "http://localhost:8092/assets";

    public AssetService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Asset getAssetByProductCode(String productCode) {
        String url = baseUrl + "/" + productCode;
        return restTemplate.getForObject(url, Asset.class);
    }
}
