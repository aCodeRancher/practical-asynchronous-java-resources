package com.java.trading.bot;

import com.java.trading.bot.models.BotState;

import java.util.concurrent.CompletableFuture;

public class Main {


    public static void main(String[] args) throws InterruptedException {

        TradingBot tradingBot = new TradingBot("demo-api-key", 10000d);

        CompletableFuture<BotState> completableFuture = tradingBot.start();

        Thread.sleep(100000000);
    }
}

