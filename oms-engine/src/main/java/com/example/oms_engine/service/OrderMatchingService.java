package com.example.oms_engine.service;

import java.time.LocalDateTime;
import java.util.concurrent.PriorityBlockingQueue;

import org.springframework.stereotype.Service;

import com.example.oms_engine.model.Asset;
import com.example.oms_engine.model.Order;
import com.example.oms_engine.model.Trade;
import com.example.oms_engine.repository.TradeRepository;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Getter
@Slf4j
public class OrderMatchingService {
    private final AssetService assetService;
    private final TradeRepository tradeRepository;

    private PriorityBlockingQueue<Order> buyOrders;
    private PriorityBlockingQueue<Order> sellOrders;

    @PostConstruct
    public void init() {
        buyOrders = new PriorityBlockingQueue<>(100, (o1, o2) -> {
            if (o1.getPrice() == o2.getPrice()) {
                return o1.getSubmissionTime().compareTo(o2.getSubmissionTime());
            }
            return Double.compare(o2.getPrice(), o1.getPrice());  // BUY - Highest price priority
        });

        sellOrders = new PriorityBlockingQueue<>(100, (o1, o2) -> {
            if (o1.getPrice() == o2.getPrice()) {
                return o1.getSubmissionTime().compareTo(o2.getSubmissionTime());
            }
            return Double.compare(o1.getPrice(), o2.getPrice());  // SELL - Lowest price priority
        });
    }

    public synchronized void addOrder(Order order) {
        Asset asset = assetService.getAssetByProductCode(order.getProductCode());
        if (asset == null || order.getQuantity() % asset.getMinOrderQuantity() != 0) {
            throw new RuntimeException("Invalid asset or quantity does not meet the minimum order requirements.");
        }

        // Set the order status to Open
        order.setStatus("Open");

        if ("BUY".equals(order.getType())) {
            matchOrder(order, sellOrders);
        } else {
            matchOrder(order, buyOrders);
        }
    }

    private synchronized void matchOrder(Order order, PriorityBlockingQueue<Order> oppositeOrders) {
        while (!oppositeOrders.isEmpty()) {
            Order oppositeOrder = oppositeOrders.peek();
            log.info("Matching: {} with {}", order, oppositeOrder);

            // Ensure the orders are for the same asset and currency
            if (!order.getProductCode().equals(oppositeOrder.getProductCode()) ||
                !order.getCurrencyCode().equals(oppositeOrder.getCurrencyCode())) {
                break;
            }

            if ("BUY".equals(order.getType()) && order.getPrice() >= oppositeOrder.getPrice() ||
                "SELL".equals(order.getType()) && order.getPrice() <= oppositeOrder.getPrice()) {

                log.info("Matched: {} with {}", order, oppositeOrder);

                // Match logic
                int matchedQuantity = Math.min(order.getQuantity(), oppositeOrder.getQuantity());
                order.setQuantity(order.getQuantity() - matchedQuantity);
                oppositeOrder.setQuantity(oppositeOrder.getQuantity() - matchedQuantity);

                Trade trade = new Trade();
                trade.setProductCode(order.getProductCode());
                trade.setQuantity(matchedQuantity);
                trade.setPrice(order.getPrice());
                trade.setCurrencyCode(order.getCurrencyCode());
                trade.setTradeTime(LocalDateTime.now());
                tradeRepository.save(trade);

                if (order.getQuantity() == 0 && oppositeOrder.getQuantity() == 0) {
                    order.setStatus("Filled");
                    oppositeOrder.setStatus("Filled");
    
                    log.info("Matched: Buy {} units of {} at ${} with Sell {} units of {} at ${}.",
                            matchedQuantity, order.getProductCode(), order.getPrice(),
                            matchedQuantity, oppositeOrder.getProductCode(), oppositeOrder.getPrice());
                } else if (order.getQuantity() == 0) {
                    order.setStatus("Filled");
                    oppositeOrder.setStatus("PartiallyFilled");
    
                    log.info("Partial Match: Buy {} units of {} at ${} with Sell {} units of {} at ${}.",
                            matchedQuantity, order.getProductCode(), order.getPrice(),
                            matchedQuantity, oppositeOrder.getProductCode(), oppositeOrder.getPrice());
                    log.info("Remaining: {} units to be matched later.", oppositeOrder.getQuantity());
                } else if (oppositeOrder.getQuantity() == 0) {
                    order.setStatus("PartiallyFilled");
                    oppositeOrder.setStatus("Filled");
    
                    log.info("Partial Match: Buy {} units of {} at ${} with Sell {} units of {} at ${}.",
                            matchedQuantity, order.getProductCode(), order.getPrice(),
                            matchedQuantity, oppositeOrder.getProductCode(), oppositeOrder.getPrice());
                    log.info("Remaining: {} units to be matched later.", order.getQuantity());
                } else {
                    order.setStatus("PartiallyFilled");
                    oppositeOrder.setStatus("PartiallyFilled");
    
                    log.info("Partial Match: Buy {} units of {} at ${} with Sell {} units of {} at ${}.",
                            matchedQuantity, order.getProductCode(), order.getPrice(),
                            matchedQuantity, oppositeOrder.getProductCode(), oppositeOrder.getPrice());
                    log.info("Remaining: {} units to be matched later.", order.getQuantity());
                }

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

    public synchronized void checkForExpiredOrders() {
        checkAndExpireOrders(buyOrders);
        checkAndExpireOrders(sellOrders);
    }

    private synchronized void checkAndExpireOrders(PriorityBlockingQueue<Order> orders) {
        LocalDateTime now = LocalDateTime.now();
        orders.removeIf(order -> {
            if (order.getSubmissionTime().isBefore(now.minusHours(48))) {
                order.setStatus("Expired");
                notifyClient(order);
                return true;
            }
            return false;
        });
    }

    private void notifyClient(Order order) {
        // Implement the callback logic to notify the client
        log.info("Client Notification - Order expired: {}", order);
    }
}