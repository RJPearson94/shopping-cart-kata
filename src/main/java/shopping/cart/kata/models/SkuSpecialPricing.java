package shopping.cart.kata.models;

import lombok.Builder;
import lombok.Getter;

@Builder
public class SkuSpecialPricing {
    @Getter
    private int quantity;

    @Getter
    private int overriddenPrice;

    @Getter
    private int priceDiscount;
}