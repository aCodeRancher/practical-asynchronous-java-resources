package com.java.trading.bot.models;


public class Order {
    private OrderType orderType;
    private Symbol symbol;
    private Integer sharesCount;
    private Double price;

    public Order(OrderType orderType, Symbol symbol, Integer sharesCount, Double price) {
        this.orderType = orderType;
        this.symbol = symbol;
        this.sharesCount = sharesCount;
        this.price = price;
    }

    public OrderType getOrderType() {
        return orderType;
    }

    public void setOrderType(OrderType orderType) {
        this.orderType = orderType;
    }

    public Symbol getSymbol() {
        return symbol;
    }

    public void setSymbol(Symbol symbol) {
        this.symbol = symbol;
    }

    public Integer getSharesCount() {
        return sharesCount;
    }

    public void setSharesCount(Integer sharesCount) {
        this.sharesCount = sharesCount;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }


    @Override
    public String toString() {
        return "Order{" +
                "orderType=" + orderType +
                ", symbol=" + symbol +
                ", sharesCount=" + sharesCount +
                ", price=" + price +
                '}';
    }
}
