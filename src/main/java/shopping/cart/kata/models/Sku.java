package shopping.cart.kata.models;

import lombok.Builder;
import lombok.Getter;

@Builder
public class Sku {
    @Getter
    private int price;

    @Getter
    private SkuSpecialPricing skuSpecialPricing;
}