package com.example.oms_client.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.oms_client.model.Client;

public interface ClientRepository extends JpaRepository<Client, Long> {
}
