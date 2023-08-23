package com.java.trading.bot.broker;

import com.java.trading.bot.broker.BrokerAPI;
import com.java.trading.bot.broker.BrokerAPIImpl;
import com.java.trading.bot.models.BotState;
import com.java.trading.bot.models.Order;
import com.java.trading.bot.models.OrderType;

import java.util.concurrent.*;

public class TradingBotBackup {

    private final BrokerAPI brokerAPI;
    private Double workingAmount;

    private static final ScheduledExecutorService threadPool = Executors.newScheduledThreadPool(4);

    public TradingBotBackup(String apiKey, Double startingUsdAmount) {
        brokerAPI = BrokerAPIImpl.getInstance(apiKey);
        this.workingAmount = startingUsdAmount;
    }

    public CompletableFuture<BotState> start() throws ExecutionException, InterruptedException {
        CompletableFuture<BotState> botState = new CompletableFuture<>();

        brokerAPI.getPrices().thenAcceptAsync(
                (prices) -> {
                    Double amountPerSymbol = this.workingAmount / prices.size();

                    prices.forEach(e -> {
                        Order order = new Order(
                                OrderType.BUY, e.getSymbol(), (int) (amountPerSymbol / e.getPrice()), e.getPrice());
                        brokerAPI.submitOrder(order);
                    });

                    workingAmount = 0d;
                })
                .thenRunAsync(() -> threadPool.scheduleWithFixedDelay(new ExecutionStep(), 3, 4, TimeUnit.SECONDS))
                .thenRunAsync(() -> botState.complete(BotState.STARTED));

        return botState;
    }

    public CompletableFuture<BotState> stop() {
        CompletableFuture<BotState> botState = new CompletableFuture<>();
        // Stop the scheduler first
        // Check all open orders and SELL everything

        return botState;
    }

    /**
     * The simplistic logic of the bot should be the following:
     * -> Every X seconds the bot looks on submitted orders and on current prices (per symbol)
     * -> If the last order was BUY and the price went up by 20%, then the bot SELLs all the shares of that stock
     * -> If the last order was SELL and the price went down by 10%, then the bot buys shares of that stock if it has money
     */
    class ExecutionStep implements Runnable {

        @Override
        public void run() {
            // Every 1 second analyze the prices and open orders
            System.out.println("New iteration, current working amount = " + workingAmount);

            brokerAPI.getPrices().thenCombineAsync(brokerAPI.getSubmittedOrders(), (prices, submittedOrders) -> {

                prices.forEach(price -> {
                    Order lastOrder = submittedOrders.stream()
                            .filter(order -> order.getSymbol().equals(price.getSymbol()))
                            .findFirst()
                            .get();

                    Double oldPrice = lastOrder.getPrice();
                    Double newPrice = price.getPrice();

                    // First case
                    if (lastOrder.getOrderType() == OrderType.BUY && oldPrice * 1.2 < newPrice) {

                        brokerAPI.submitOrder(new Order(OrderType.SELL, price.getSymbol(), lastOrder.getSharesCount(), newPrice));
                        workingAmount += newPrice * lastOrder.getSharesCount();

                    // Second case
                    } else if (lastOrder.getOrderType() == OrderType.SELL && oldPrice * 0.9 > newPrice) {

                        int shares = (int) (workingAmount / newPrice);

                        if (shares < 1) {
                            System.out.println("No money to buy shares for " + price.getSymbol() + ", skipping iteration");
                        } else {
                            brokerAPI.submitOrder(new Order(OrderType.BUY, price.getSymbol(), shares, newPrice));
                            workingAmount -= newPrice * shares;
                        }
                    } else {
                        System.out.println("Doing nothing because no condition is met, prices = " + prices);
                    }
                });
                return null;
            });
        }
    }
}
