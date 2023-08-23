package com.java.trading.bot.broker;

import com.java.trading.bot.models.Order;
import com.java.trading.bot.models.OrderResponse;
import com.java.trading.bot.models.Price;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public interface BrokerAPI {
    CompletableFuture<OrderResponse> submitOrder(Order order);

    CompletableFuture<Set<Order>> getSubmittedOrders();

    CompletableFuture<Set<Price>> getPrices();
}