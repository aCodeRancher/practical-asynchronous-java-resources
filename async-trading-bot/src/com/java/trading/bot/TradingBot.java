package com.java.trading.bot;

import com.java.trading.bot.broker.BrokerAPI;
import com.java.trading.bot.broker.BrokerAPIImpl;
import com.java.trading.bot.models.BotState;
import com.java.trading.bot.models.Order;
import com.java.trading.bot.models.OrderType;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TradingBot {

    private final BrokerAPI brokerAPI;
    private Double workingAmount;

    private final ScheduledExecutorService threadPoolExecutor = Executors.newScheduledThreadPool(4);

    public TradingBot(String apiKey, Double initialAmount) {
        brokerAPI = BrokerAPIImpl.getInstance(apiKey);
        workingAmount = initialAmount;
    }

    public CompletableFuture<BotState> start() {
        CompletableFuture<BotState> botStateCompletableFuture = new CompletableFuture<>();

        // Split the amount evenly on all the symbols
        brokerAPI.getPrices().thenAcceptAsync(prices -> {

            Double amountPerSymbol = workingAmount / prices.size();

            prices.forEach(price -> {
                Order order = new Order(
                        OrderType.BUY, price.getSymbol(), (int)(amountPerSymbol / price.getPrice()), price.getPrice()
                );
                brokerAPI.submitOrder(order);
            });

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            workingAmount = 0d;
        }).thenRunAsync(() -> threadPoolExecutor.scheduleAtFixedRate(new ExecutionStep(), 3, 4, TimeUnit.SECONDS))
        .thenRunAsync(() -> botStateCompletableFuture.complete(BotState.STARTED));

        return botStateCompletableFuture;
    }

    public void stop() {
        // Shutdown the thread-pool
        // One final check for the profit to see if we need to sell the shares or keep them
    }

    class ExecutionStep implements Runnable {

        @Override
        public void run() {
            System.out.println("Current working amount: " + workingAmount);

            brokerAPI.getPrices().thenCombineAsync(brokerAPI.getSubmittedOrders(), (prices, orders) -> {

                prices.forEach(price -> {
                    Order lastOrder = orders.stream().filter(order -> order.getSymbol() == price.getSymbol())
                            .findFirst()
                            .get();

                    Double oldPrice = lastOrder.getPrice();
                    Double newPrice = price.getPrice();

                    if (lastOrder.getOrderType() == OrderType.BUY && newPrice > oldPrice * 1.2) {
                        // Sell shares
                        Order order = new Order(OrderType.SELL, lastOrder.getSymbol(), lastOrder.getSharesCount(), newPrice);
                        brokerAPI.submitOrder(order);
                        workingAmount += lastOrder.getSharesCount() * newPrice;

                    } else if (lastOrder.getOrderType() == OrderType.SELL && newPrice < oldPrice * 0.9) {
                        // Buy shares

                        int numberOfShares = (int) (workingAmount / newPrice);

                        if (numberOfShares < 1) {
                            System.out.println("We don't have money to buy shares, skipping iteration");
                        } else {
                            Order order = new Order(OrderType.BUY, lastOrder.getSymbol(), numberOfShares, newPrice);
                            brokerAPI.submitOrder(order);
                            workingAmount -= numberOfShares * newPrice;
                        }
                    } else {
                        System.out.println("Nothing happens, continuing");
                    }
                });

                return null;
            });
        }
    }
}
