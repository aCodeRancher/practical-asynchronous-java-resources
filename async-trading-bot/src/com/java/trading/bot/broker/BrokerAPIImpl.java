package com.java.trading.bot.broker;

import com.java.trading.bot.models.Order;
import com.java.trading.bot.models.OrderResponse;
import com.java.trading.bot.models.Price;
import com.java.trading.bot.models.Symbol;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class BrokerAPIImpl implements BrokerAPI {

    private static BrokerAPI instance;

    private final Set<Order> orders = new HashSet<>();

    /**
     * Map from symbol to (currentPrice, price direction)
     * Price direction is a boolean that tells whether the price should be increased or decreased on the next iteration
     */
    private final Map<Symbol, Map.Entry<Double, Boolean>> currentPrices = new HashMap<>();

    /**
     * Map from symbol to (minPriceLimit, maxPriceLimit)
     *
     * This is the interval in which the price of that symbol will play
     */
    private final Map<Symbol, Map.Entry<Double, Double>> priceLimits = new HashMap<>();

    private BrokerAPIImpl(String apiKey) {
        if (!apiKey.equals("demo-api-key")) {
            throw new IllegalStateException("Wrong API key");
        }

        initPrices();
    }

    private void initPrices() {
        priceLimits.put(Symbol.AMZN, new AbstractMap.SimpleEntry<>(10d, 400d));
        priceLimits.put(Symbol.TSLA, new AbstractMap.SimpleEntry<>(100d, 400d));
        priceLimits.put(Symbol.KO, new AbstractMap.SimpleEntry<>(200d, 400d));
    }

    public static BrokerAPI getInstance(String apiKey) {
        if (instance == null) {
            instance = new BrokerAPIImpl(apiKey);
        }
        return instance;
    }

    @Override
    public CompletableFuture<OrderResponse> submitOrder(Order order) {
        System.out.println("Order submitted: " + order);
        String orderId = UUID.randomUUID().toString();

        orders.removeIf(e -> e.getSymbol() == order.getSymbol());
        orders.add(order);

        return CompletableFuture.completedFuture(new OrderResponse(orderId));
    }

    @Override
    public CompletableFuture<Set<Order>> getSubmittedOrders() {
        return CompletableFuture.completedFuture(orders);
    }

    @Override
    public CompletableFuture<Set<Price>> getPrices() {
        for (Symbol symbol : Symbol.values()) {

            Map.Entry<Double, Boolean> priceDirection = currentPrices.get(symbol);

            // If no entry, I add the min price and price change direction will be UP (so it will raise)
            if (priceDirection == null) {
                currentPrices.put(symbol, new AbstractMap.SimpleEntry<>(priceLimits.get(symbol).getKey(), true));
            } else {
                // If price direction is true, then we need to increase the price (until it reaches the max limit)
                if (priceDirection.getValue()) {
                    Double newPrice = priceDirection.getKey() + priceDirection.getKey() * 0.05;

                    // If the new price is greater than the max limit
                    if (newPrice > priceLimits.get(symbol).getValue()) {
                        // Switch the direction and keep the max price
                        currentPrices.put(symbol, new AbstractMap.SimpleEntry<>(priceLimits.get(symbol).getValue(), false));
                    } else {
                        // We haven't reach the limit, so we go up
                        currentPrices.put(symbol, new AbstractMap.SimpleEntry<>(newPrice, true));
                    }
                } else {
                    Double newPrice = priceDirection.getKey() - priceDirection.getKey() * 0.05;

                    // If the new price is smaller than the min limit
                    if (newPrice < priceLimits.get(symbol).getKey()) {
                        // Switch the direction and keep the min price
                        currentPrices.put(symbol, new AbstractMap.SimpleEntry<>(priceLimits.get(symbol).getKey(), true));
                    } else {
                        // We haven't reach the limit, so we go down
                        currentPrices.put(symbol, new AbstractMap.SimpleEntry<>(newPrice, false));
                    }
                }
            }
        }

        return CompletableFuture.completedFuture(currentPrices.entrySet().stream().map(
                entry -> new Price(entry.getKey(), entry.getValue().getKey())
        ).collect(Collectors.toSet()));
    }
}
