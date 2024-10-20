package com.example.oms_engine.service;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.example.oms_engine.model.Asset;
import com.example.oms_engine.model.Order;
import com.example.oms_engine.repository.TradeRepository;

public class OrderMatchingServiceTest {

    @Mock
    private AssetService assetService;

    @Mock
    private TradeRepository tradeRepository;

    @InjectMocks
    private OrderMatchingService orderMatchingService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        orderMatchingService.init();
    }

    @Test
    public void testInit() {
        assertNotNull(orderMatchingService);
        assertNotNull(orderMatchingService.getBuyOrders());
        assertTrue(orderMatchingService.getBuyOrders().isEmpty());
        assertNotNull(orderMatchingService.getSellOrders());
        assertTrue(orderMatchingService.getSellOrders().isEmpty());
    }

    @Test
    public void testAddOrder_InvalidAsset() {
        Order order = new Order();
        order.setProductCode("INVALID");
        order.setQuantity(10);
        order.setType("BUY");

        when(assetService.getAssetByProductCode("INVALID")).thenReturn(null);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            orderMatchingService.addOrder(order);
        });

        assertEquals("Invalid asset or quantity does not meet the minimum order requirements.", exception.getMessage());
    }

    @Test
    public void testAddOrder_InvalidQuantity() {
        Order order = new Order();
        order.setProductCode("VALID");
        order.setQuantity(15);  // Not a multiple of minOrderQuantity
        order.setType("BUY");

        Asset asset = new Asset();
        asset.setMinOrderQuantity(10);

        when(assetService.getAssetByProductCode("VALID")).thenReturn(asset);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            orderMatchingService.addOrder(order);
        });

        assertEquals("Invalid asset or quantity does not meet the minimum order requirements.", exception.getMessage());
    }

    @Test
    public void testAddOrder_ValidBuyOrder() {
        Order order = new Order();
        order.setProductCode("VALID");
        order.setQuantity(10);
        order.setType("BUY");
        order.setPrice(100.0);
        order.setCurrencyCode("USD");

        Asset asset = new Asset();
        asset.setMinOrderQuantity(10);

        when(assetService.getAssetByProductCode("VALID")).thenReturn(asset);

        orderMatchingService.addOrder(order);

        assertFalse(orderMatchingService.getBuyOrders().isEmpty());
        assertTrue(orderMatchingService.getSellOrders().isEmpty());
        assertEquals("Open", order.getStatus());
    }

    @Test
    public void testAddOrder_ValidSellOrder() {
        Order order = new Order();
        order.setProductCode("VALID");
        order.setQuantity(10);
        order.setType("SELL");
        order.setPrice(100.0);
        order.setCurrencyCode("USD");

        Asset asset = new Asset();
        asset.setMinOrderQuantity(10);

        when(assetService.getAssetByProductCode("VALID")).thenReturn(asset);

        orderMatchingService.addOrder(order);

        assertFalse(orderMatchingService.getSellOrders().isEmpty());
        assertTrue(orderMatchingService.getBuyOrders().isEmpty());
        assertEquals("Open", order.getStatus());
    }

    @Test
    public void testMatchOrder_FullyMatched() {
        Order buyOrder = new Order();
        buyOrder.setProductCode("VALID");
        buyOrder.setQuantity(10);
        buyOrder.setType("BUY");
        buyOrder.setPrice(100.0);
        buyOrder.setCurrencyCode("USD");

        Order sellOrder = new Order();
        sellOrder.setProductCode("VALID");
        sellOrder.setQuantity(10);
        sellOrder.setType("SELL");
        sellOrder.setPrice(100.0);
        sellOrder.setCurrencyCode("USD");

        Asset asset = new Asset();
        asset.setMinOrderQuantity(10);

        when(assetService.getAssetByProductCode("VALID")).thenReturn(asset);

        orderMatchingService.addOrder(sellOrder);
        orderMatchingService.addOrder(buyOrder);

        assertTrue(orderMatchingService.getBuyOrders().isEmpty());
        assertTrue(orderMatchingService.getSellOrders().isEmpty());
        assertEquals("Filled", buyOrder.getStatus());
        assertEquals("Filled", sellOrder.getStatus());
    }

    @Test
    public void testMatchOrder_PartiallyMatched() {
        Order buyOrder = new Order();
        buyOrder.setProductCode("VALID");
        buyOrder.setQuantity(15);
        buyOrder.setType("BUY");
        buyOrder.setPrice(100.0);
        buyOrder.setCurrencyCode("USD");

        Order sellOrder = new Order();
        sellOrder.setProductCode("VALID");
        sellOrder.setQuantity(10);
        sellOrder.setType("SELL");
        sellOrder.setPrice(100.0);
        sellOrder.setCurrencyCode("USD");

        Asset asset = new Asset();
        asset.setMinOrderQuantity(5);

        when(assetService.getAssetByProductCode("VALID")).thenReturn(asset);

        orderMatchingService.addOrder(sellOrder);
        orderMatchingService.addOrder(buyOrder);

        assertFalse(orderMatchingService.getBuyOrders().isEmpty());
        assertTrue(orderMatchingService.getSellOrders().isEmpty());
        assertEquals(5, orderMatchingService.getBuyOrders().peek().getQuantity());
        assertEquals("PartiallyFilled", buyOrder.getStatus());
        assertEquals("Filled", sellOrder.getStatus());
    }

    @Test
    public void testMatchOrder_DifferentCurrency() {
        Order buyOrder = new Order();
        buyOrder.setProductCode("VALID");
        buyOrder.setQuantity(10);
        buyOrder.setType("BUY");
        buyOrder.setPrice(100.0);
        buyOrder.setCurrencyCode("USD");

        Order sellOrder = new Order();
        sellOrder.setProductCode("VALID");
        sellOrder.setQuantity(10);
        sellOrder.setType("SELL");
        sellOrder.setPrice(100.0);
        sellOrder.setCurrencyCode("EUR");

        Asset asset = new Asset();
        asset.setMinOrderQuantity(10);

        when(assetService.getAssetByProductCode("VALID")).thenReturn(asset);

        orderMatchingService.addOrder(sellOrder);
        orderMatchingService.addOrder(buyOrder);

        assertFalse(orderMatchingService.getBuyOrders().isEmpty());
        assertFalse(orderMatchingService.getSellOrders().isEmpty());
        assertEquals("Open", buyOrder.getStatus());
        assertEquals("Open", sellOrder.getStatus());
    }

    @Test
    public void testCheckForExpiredOrders_ExpireBuyOrder() {
        Order buyOrder = new Order();
        buyOrder.setProductCode("VALID");
        buyOrder.setQuantity(10);
        buyOrder.setType("BUY");
        buyOrder.setPrice(100.0);
        buyOrder.setCurrencyCode("USD");
        buyOrder.setSubmissionTime(LocalDateTime.now().minusHours(49));

        Asset asset = new Asset();
        asset.setMinOrderQuantity(10);

        when(assetService.getAssetByProductCode("VALID")).thenReturn(asset);

        orderMatchingService.addOrder(buyOrder);
        orderMatchingService.checkForExpiredOrders();

        assertTrue(orderMatchingService.getBuyOrders().isEmpty());
        assertEquals("Expired", buyOrder.getStatus());
        verify(assetService).getAssetByProductCode("VALID");
    }

    @Test
    public void testCheckForExpiredOrders_ExpireSellOrder() {
        Order sellOrder = new Order();
        sellOrder.setProductCode("VALID");
        sellOrder.setQuantity(10);
        sellOrder.setType("SELL");
        sellOrder.setPrice(100.0);
        sellOrder.setCurrencyCode("USD");
        sellOrder.setSubmissionTime(LocalDateTime.now().minusHours(49));

        Asset asset = new Asset();
        asset.setMinOrderQuantity(10);

        when(assetService.getAssetByProductCode("VALID")).thenReturn(asset);

        orderMatchingService.addOrder(sellOrder);
        orderMatchingService.checkForExpiredOrders();

        assertTrue(orderMatchingService.getSellOrders().isEmpty());
        assertEquals("Expired", sellOrder.getStatus());
        verify(assetService).getAssetByProductCode("VALID");
    }

    @Test
    public void testCheckForExpiredOrders_NoOrdersExpired() {
        Order buyOrder = new Order();
        buyOrder.setProductCode("S001");
        buyOrder.setQuantity(10);
        buyOrder.setType("BUY");
        buyOrder.setPrice(100.0);
        buyOrder.setCurrencyCode("SGD");
        buyOrder.setSubmissionTime(LocalDateTime.now().minusHours(47));

        Order sellOrder = new Order();
        sellOrder.setProductCode("U001");
        sellOrder.setQuantity(10);
        sellOrder.setType("SELL");
        sellOrder.setPrice(100.0);
        sellOrder.setCurrencyCode("USD");
        sellOrder.setSubmissionTime(LocalDateTime.now().minusHours(47));

        Asset asset = new Asset();
        asset.setMinOrderQuantity(10);

        when(assetService.getAssetByProductCode("S001")).thenReturn(asset);
        when(assetService.getAssetByProductCode("U001")).thenReturn(asset);

        orderMatchingService.addOrder(buyOrder);
        orderMatchingService.addOrder(sellOrder);
        orderMatchingService.checkForExpiredOrders();

        assertFalse(orderMatchingService.getBuyOrders().isEmpty());
        assertFalse(orderMatchingService.getSellOrders().isEmpty());
        assertEquals("Open", buyOrder.getStatus());
        assertEquals("Open", sellOrder.getStatus());
        verify(assetService, times(1)).getAssetByProductCode("S001");
        verify(assetService, times(1)).getAssetByProductCode("U001");
    }
}