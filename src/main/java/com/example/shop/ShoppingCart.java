package com.example.shop;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ShoppingCart {
    private final List<Item> items = new ArrayList<>();


    public void addItem(Item newItem) {
        items.stream()
                .filter(item -> item.getName().equals(newItem.getName()))
                .findFirst()
                .ifPresentOrElse(
                        existing -> existing.setQuantity(existing.getQuantity() + newItem.getQuantity()),
                        () -> items.add(newItem)
                );
    }

    public List<Item> getItems() {
        return items;
    }

    public BigDecimal getTotalPrice() {
        BigDecimal totalPrice = BigDecimal.ZERO;
        for(Item item : items) {
            BigDecimal itemTotal = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            totalPrice = totalPrice.add(itemTotal);
        }
        return totalPrice;
    }

    public void removeItem(Item item) {
        items.remove(item);
    }
}
