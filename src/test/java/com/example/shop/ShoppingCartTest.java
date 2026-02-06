package com.example.shop;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class ShoppingCartTest {

    @Mock
    DiscountService discountService;

    @Test
    void shouldReturnSizeOneWhenOneItemIsAdded() {
        ShoppingCart cart = new ShoppingCart();
        Item item = new Item("Football", new BigDecimal("150.0"), 1);
        cart.addItem(item);
        assertEquals(1, cart.getItems().size());
    }

    @Test
    void shouldCalculateTotalPriceForMultipleItemsOfOneQuantity() {
        ShoppingCart cart = new ShoppingCart();
        cart.addItem(new Item("Football", new BigDecimal("150.0"), 1));
        cart.addItem(new Item("Socks", new BigDecimal("50.0"), 1));

        assertEquals(new BigDecimal("200.0"), cart.getTotalPrice());
    }

    @Test
    void shouldRemoveItemAndUpdateTotalWhenItemIsRemoved() {
        ShoppingCart cart = new ShoppingCart();
        Item football = new Item("Football", new BigDecimal("150.0"), 1);
        Item socks = new Item("Socks", new BigDecimal("50.0"), 1);

        cart.addItem(football);
        cart.addItem(socks);

        cart.removeItem(football);

        assertAll(
                () -> assertEquals(1, cart.getItems().size(), "Number of items is incorrect"),
                () -> assertEquals(0, new BigDecimal("50.0")
                        .compareTo(cart.getTotalPrice()), "Total price is incorrect")
        );

    }

    @Test
    void shouldIncreaseQuantityWhenAddingSameItem() {
        ShoppingCart cart = new ShoppingCart();
        Item football = new Item("Football", new BigDecimal("150.0"), 1);

        cart.addItem(football);
        cart.addItem(football);

        assertEquals(1, cart.getItems().size(), "Should not add new row of the same item");
        assertEquals(2, cart.getItems().getFirst().getQuantity(), "Quantity should be two");
        assertEquals(0, new BigDecimal("300.0").compareTo(cart.getTotalPrice()), "Price should reflect total quantity");

    }

    @Test
    void shouldApplyDiscountToPrice() {
        DiscountService discountServiceMock = Mockito.mock(DiscountService.class);

        Mockito.when(discountServiceMock.getDiscountMultiplier("Handball")).thenReturn(new BigDecimal("0.9"));

        ShoppingCart cart = new ShoppingCart(discountServiceMock);
        cart.addItem(new Item("Handball", new BigDecimal("100.0"), 1));

        assertEquals(0, new BigDecimal("90").compareTo(cart.getTotalPrice()), "Discount wasn't applied correctly");
    }

    @Test
    void shouldReturnZeroPriceWhenCartIsEmpty() {
        ShoppingCart cart = new ShoppingCart();
        assertEquals(0, BigDecimal.ZERO.compareTo(cart.getTotalPrice()));
    }
}
