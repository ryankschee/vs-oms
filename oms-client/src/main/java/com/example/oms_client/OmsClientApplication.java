package com.example.oms_client;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.example.oms_client.simulator.OrderSimulator;

@SpringBootApplication
public class OmsClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(OmsClientApplication.class, args);
	}

    @Bean
    public ApplicationRunner applicationRunner() {
        return args -> {
            OrderSimulator.simulateOrders();
        };
    }
}
