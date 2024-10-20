package com.example.oms_client.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.oms_client.model.Client;
import com.example.oms_client.repository.ClientRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/clients")
@RequiredArgsConstructor
public class ClientController {
    private final ClientRepository clientRepository;

    @PostMapping("/register")
    public ResponseEntity<Client> registerClient(@RequestBody Client client) {
        return ResponseEntity.ok(clientRepository.save(client));
    }

    @GetMapping("/all")
    public ResponseEntity<List<Client>> getAllClients() {
        return ResponseEntity.ok(clientRepository.findAll());
    }
}
