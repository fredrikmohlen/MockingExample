package com.example.shop;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertAll;
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

    @Test
    void ShouldRemoveItemAndUpdateTotalWhenItemIsRemoved(){
        ShoppingCart cart = new ShoppingCart();
        Item football = new Item("Football", new BigDecimal("150.0"),1);
        Item socks = new Item("Socks", new BigDecimal("50.0"),1);

        cart.addItem(football);
        cart.addItem(socks);

        cart.removeItem(football);

        assertAll(
                ()-> assertEquals(1, cart.getItems().size(),"Number of items is incorrect"),
                ()-> assertEquals(0, new BigDecimal("50.0")
                        .compareTo(cart.getTotalPrice()),"Total price is incorrect")
        );

    }

}
