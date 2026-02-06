package com.example.shop;

import java.math.BigDecimal;
import java.util.Objects;

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

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getDiscountPercentage() {
        return discountPercentage;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Item item)) return false;
        return quantity == item.quantity && Objects.equals(name, item.name) && Objects.equals(price, item.price) && Objects.equals(discountPercentage, item.discountPercentage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, price, quantity, discountPercentage);
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
