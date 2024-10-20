package com.example.oms_client.simulator;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OrderSimulator {

    private static final String ORDER_API_URL = "http://localhost:8090/orders/submission";
    private static final int THREAD_COUNT = 5;
    private static final int SIMULATION_DURATION_SECONDS = 30;
    private static final String ORDERS_FILE_PATH = "orders/order-01.json"; // Path to the JSON file in resources

    public static void simulateOrders() throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        HttpClient client = HttpClient.newHttpClient();
        ObjectMapper objectMapper = new ObjectMapper();

        // Read orders from JSON file
        List<Order> orders = readOrdersFromFile(objectMapper, ORDERS_FILE_PATH);
        log.info(ORDERS_FILE_PATH + " contains " + orders.size() + " orders");

        for (int i = 0; i < orders.size(); i++) {
            Order order = orders.get(i);
            executorService.submit(() -> {
                try {
                    String orderJson = objectMapper.writeValueAsString(order);

                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(new URI(ORDER_API_URL))
                            .header("Content-Type", "application/json")
                            .POST(HttpRequest.BodyPublishers.ofString(orderJson))
                            .build();

                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                    log.info("Client {} submitted order: {} Response: {}", order.getClientId(), orderJson, response.body());
                } catch (IOException | InterruptedException | URISyntaxException e) {
                    log.error("Error submitting order", e);
                }
            });
        }

        executorService.shutdown();
        executorService.awaitTermination(SIMULATION_DURATION_SECONDS + 5, TimeUnit.SECONDS);
    }

    private static List<Order> readOrdersFromFile(ObjectMapper objectMapper, String filePath) throws IOException {
        ClassLoader classLoader = OrderSimulator.class.getClassLoader();
        File file = new File(classLoader.getResource(filePath).getFile());
        return objectMapper.readValue(file, new TypeReference<List<Order>>() {});
    }

    @Data
    static class Order {
        private int clientId;
        private String productCode;
        private int quantity;
        private String type;
        private double price;
        private String currencyCode;
    }
}
