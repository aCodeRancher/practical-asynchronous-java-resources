package com.java.trading.bot.models;

public class Price {
    private Symbol symbol;
    private Double price;

    public Price(Symbol symbol, Double price) {
        this.symbol = symbol;
        this.price = price;
    }

    public Symbol getSymbol() {
        return symbol;
    }

    public void setSymbol(Symbol symbol) {
        this.symbol = symbol;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Price{" +
                "symbol=" + symbol +
                ", price=" + price +
                '}';
    }
}
