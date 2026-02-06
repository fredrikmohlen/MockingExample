package com.example.shop;

import java.math.BigDecimal;

public class Item {

    private final String name;
    private final BigDecimal price;
    private int quantity;
    private BigDecimal discountPercentage;
    public Item(String name, BigDecimal price, int quantity) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.discountPercentage = BigDecimal.ZERO;
    }


}
