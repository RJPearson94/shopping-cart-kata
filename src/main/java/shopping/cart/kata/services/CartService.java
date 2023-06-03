package shopping.cart.kata.services;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import lombok.Getter;
import shopping.cart.kata.exceptions.ItemNotFoundException;
import shopping.cart.kata.models.Sku;
import shopping.cart.kata.models.SkuSpecialPricing;

public class CartService {
  private final Map<String, Sku> skus;

  @Getter
  private final Map<String, Integer> scannedItems;

  @Getter
  private int runningTotal;

  @Getter
  private int totalDiscount;

  public CartService(Map<String, Sku> skus) {
    this.skus = skus;
    this.scannedItems = new HashMap<>();
    this.runningTotal = 0;
    this.totalDiscount = 0;
  }

  public void addItem(String item) {
    final Sku sku = Optional
      .ofNullable(this.skus.get(item))
      .orElseThrow(() -> new ItemNotFoundException(String.format("No SKU found for item (%s)", item)));

    final int updatedQuantity = Optional.ofNullable(this.scannedItems.get(item)).orElse(0) + 1;
    this.scannedItems.put(item, updatedQuantity);

    this.runningTotal += sku.getPrice();

    final int discountToBeApplied = this.determineDiscountToBeApplied(sku, updatedQuantity);
    this.totalDiscount += discountToBeApplied;
  }

  private int determineDiscountToBeApplied(Sku sku, int quantity) {
    return Optional.ofNullable(sku.getSkuSpecialPricing())
      .filter(skuSpecialPricing -> quantity % skuSpecialPricing.getQuantity() == 0)
      .map(SkuSpecialPricing::getPriceDiscount)
      .orElse(0);
  }
}