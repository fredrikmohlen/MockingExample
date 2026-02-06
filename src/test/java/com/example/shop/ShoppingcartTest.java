package com.example.shop;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ShoppingcartTest {

    @Test
    void ShouldReturnSizeOneWhenOneItemIsAdded(){
        ShoppingCart cart = new ShoppingCart();
        Item item = new Item("Football", new BigDecimal("150.0"),1);
        cart.addItem(item);
        assertEquals(1, cart.getItems().size());
    }

    @Test
    void ShouldCalculateTotalPriceForMultipleItemsOfOneQuantity(){
        ShoppingCart cart = new ShoppingCart();
        cart.addItem(new Item("Football", new BigDecimal("150.0"),1));
        cart.addItem(new Item("Socks", new BigDecimal("50.0"),1));

        assertEquals(new BigDecimal("200.0"), cart.getTotalPrice());
    }

}
