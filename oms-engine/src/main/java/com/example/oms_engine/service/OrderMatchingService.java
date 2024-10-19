package com.example.oms_engine.service;

import java.util.PriorityQueue;

import org.springframework.stereotype.Service;

import com.example.oms_engine.model.Order;

import jakarta.annotation.PostConstruct;

@Service
public class OrderMatchingService {
    private PriorityQueue<Order> buyOrders;
    private PriorityQueue<Order> sellOrders;

    @PostConstruct
    public void init() {
        buyOrders = new PriorityQueue<>((o1, o2) -> {
            if (o1.getPrice() == o2.getPrice()) {
                return o1.getSubmissionTime().compareTo(o2.getSubmissionTime());
            }
            return Double.compare(o2.getPrice(), o1.getPrice());  // Highest price priority
        });

        sellOrders = new PriorityQueue<>((o1, o2) -> {
            if (o1.getPrice() == o2.getPrice()) {
                return o1.getSubmissionTime().compareTo(o2.getSubmissionTime());
            }
            return Double.compare(o1.getPrice(), o2.getPrice());  // Lowest price priority
        });
    }

    public void addOrder(Order order) {
        if ("BUY".equals(order.getType())) {
            matchOrder(order, sellOrders);
        } else {
            matchOrder(order, buyOrders);
        }
    }

    private void matchOrder(Order order, PriorityQueue<Order> oppositeOrders) {
        while (!oppositeOrders.isEmpty()) {
            Order oppositeOrder = oppositeOrders.peek();

            if ("BUY".equals(order.getType()) && order.getPrice() >= oppositeOrder.getPrice() ||
                "SELL".equals(order.getType()) && order.getPrice() <= oppositeOrder.getPrice()) {

                // Match logic
                int matchedQuantity = Math.min(order.getQuantity(), oppositeOrder.getQuantity());
                order.setQuantity(order.getQuantity() - matchedQuantity);
                oppositeOrder.setQuantity(oppositeOrder.getQuantity() - matchedQuantity);

                if (oppositeOrder.getQuantity() == 0) {
                    oppositeOrders.poll();
                }

                if (order.getQuantity() == 0) {
                    return;  // Order fully matched
                }
            } else {
                break;
            }
        }

        if (order.getQuantity() > 0) {
            if ("BUY".equals(order.getType())) {
                buyOrders.offer(order);
            } else {
                sellOrders.offer(order);
            }
        }
    }
}
