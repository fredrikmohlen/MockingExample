package com.example.shop;

import java.math.BigDecimal;

public interface DiscountService {
    BigDecimal getDiscountMultiplier(String itemName);
}
