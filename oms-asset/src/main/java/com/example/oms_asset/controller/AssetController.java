package com.example.oms_asset.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.oms_asset.model.Asset;
import com.example.oms_asset.repository.AssetRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/assets")
@RequiredArgsConstructor
public class AssetController {
    private final AssetRepository assetRepository;

    @PostMapping("/")
    public ResponseEntity<Asset> addAsset(@RequestBody Asset asset) {
        return ResponseEntity.ok(assetRepository.save(asset));
    }

    @GetMapping("/")
    public ResponseEntity<List<Asset>> getAllAssets() {
        return ResponseEntity.ok(assetRepository.findAll());
    }

    @GetMapping("/{productCode}")
    public ResponseEntity<Asset> getAssetByProductCode(@PathVariable String productCode) {
        return assetRepository.findByProductCode(productCode)
                .map(asset -> new ResponseEntity<>(asset, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
