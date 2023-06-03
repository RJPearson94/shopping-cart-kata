package shopping.cart.kata.services;

import shopping.cart.kata.exceptions.ItemNotFoundException;
import shopping.cart.kata.models.Sku;
import shopping.cart.kata.models.SkuSpecialPricing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CartServiceTests {

  private CartService cartService;

  @BeforeEach
  public void setup() {
    final Map<String, Sku> skus = new HashMap<>();
    skus.put(
        "A",
        Sku.builder()
            .price(50)
            .skuSpecialPricing(
                    SkuSpecialPricing.builder()
                        .quantity(3)
                        .overriddenPrice(130)
                        .priceDiscount(20)
                        .build()
                )
            .build()
    );
    skus.put(
        "B",
        Sku.builder()
            .price(30)
            .skuSpecialPricing(

                    SkuSpecialPricing.builder()
                        .quantity(2)
                        .overriddenPrice(45)
                        .priceDiscount(15)
                        .build()
                )
            .build()
    );
    skus.put("C", Sku.builder().price(20).build());
    skus.put("D", Sku.builder().price(15).build());

    this.cartService = new CartService(skus);
  }

  @Test
  public void scanSingleItem() {
    // When
    this.cartService.addItem("A");

    // Then
    assertEquals(50, this.cartService.getRunningTotal());
    assertEquals(0, this.cartService.getTotalDiscount());
  }

  @Test
  public void scanMultipleItems() {
    // When
    this.cartService.addItem("A");
    this.cartService.addItem("B");

    // Then
    assertEquals(80, this.cartService.getRunningTotal());
    assertEquals(0, this.cartService.getTotalDiscount());
  }

  @Test
  public void scanTheSameItemMultipleTimesToGetDiscount() {
    // When
    this.cartService.addItem("A");
    this.cartService.addItem("A");
    this.cartService.addItem("A");

    // Then
    assertEquals(150, this.cartService.getRunningTotal());
    assertEquals(20, this.cartService.getTotalDiscount());
  }

  @Test
  public void scanItemsToGetDiscount() {
    // When
    this.cartService.addItem("B");
    this.cartService.addItem("A");
    this.cartService.addItem("B");

    // Then
    assertEquals(110, this.cartService.getRunningTotal());
    assertEquals(15, this.cartService.getTotalDiscount());
  }

  @Test
  public void shouldThrowItemNotFoundExceptionWhenInvalidSkuIsSupplied() {
    // When
    // Then
    assertThrows(ItemNotFoundException.class, () -> this.cartService.addItem("Z"));
  }
}
