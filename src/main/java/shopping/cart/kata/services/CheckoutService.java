package shopping.cart.kata.services;

import shopping.cart.kata.annotations.VisibleForTesting;
import shopping.cart.kata.exceptions.ItemNotFoundException;
import shopping.cart.kata.models.Sku;
import shopping.cart.kata.models.SkuSpecialPricing;

import java.io.InputStream;
import java.io.PrintStream;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;

public class CheckoutService {
  private final Scanner scanner;

  private final PrintStream out;

  public CheckoutService(InputStream in, PrintStream out) {
    this.scanner = new Scanner(in);
    this.out = out;
  }

  public void run() {
    this.out.println("Welcome to kata shopping basket by Rob Pearson");
    this.out.println();

    final Map<String, Sku> skus = this.configureSkus();
    final CartService cartService = new CartService(skus);

    this.scanItems(cartService);
    this.outputSummary(cartService);
  }

  @VisibleForTesting
  protected Map<String, Sku> configureSkus() {
    final Map<String, Sku> skus = new HashMap<>();

    this.out.println("Before we start I need you to configure the SKUs");
    this.out.println();

    boolean shouldAddMoreSkus;
    do {
      this.out.println("Please enter the ID of the item");
      final String id = this.collectInput((String temp) -> !skus.containsKey(temp), "The ID supplied has already been configured. Please enter a unique ID of the item");

      final Sku.SkuBuilder skuBuilder = Sku.builder();

      this.out.println("Please enter the price of the item (in pence)");
      final int pricePerItem = this.collectNumericInput((Integer value) -> value > 0, "Please enter the price of the item (in pence). The value must be greater than 0");
      skuBuilder.price(pricePerItem);

      this.out.println("Does the item have promotional pricing?. Please enter Y or N");
      final String promotionalPricing = this.collectYOrNInput("Does the item have promotional pricing?. Please enter Y or N");

      if ("Y".equals(promotionalPricing)) {
        final SkuSpecialPricing.SkuSpecialPricingBuilder skuSpecialPricingBuilder = SkuSpecialPricing.builder();

        this.out.println("Please enter the number of items required to qualify for the discount");
        final int quantity = this.collectNumericInput((Integer value) -> value > 0, "Please enter the number of items required to qualify for the discount. The value must be greater than 0");
        skuSpecialPricingBuilder.quantity(quantity);

        this.out.println("Please enter the promotional price (in pence)");
        final int pricePriorToDiscount = pricePerItem * quantity;
        final int promotionalPrice = this.collectNumericInput((Integer value) -> value > 0 && pricePriorToDiscount > value, "Please enter the promotional price (in pence). The price must be greater than 0 and less than the full price of all items");
        skuSpecialPricingBuilder.overriddenPrice(promotionalPrice);
        skuSpecialPricingBuilder.priceDiscount(pricePriorToDiscount - promotionalPrice);

        skuBuilder.skuSpecialPricing(skuSpecialPricingBuilder.build());
      }

      skus.put(id, skuBuilder.build());

      this.out.println("I have stored that item. Would you like to configure any more SKUs? Please enter Y or N");
      final String shouldAddMore = this.collectYOrNInput("Would you like to configure any more SKUs? Please enter Y or N");
      shouldAddMoreSkus = "Y".equals(shouldAddMore);
    } while (shouldAddMoreSkus);
    return skus;
  }

  @VisibleForTesting
  protected void scanItems(CartService cartService) {
    this.out.println("SKUs have been configured. Please proceed to scanning items");
    this.out.println();

    do {
      this.out.println("Please scan your next item. Press enter when all items have been scanned");
      final String scannedItem = this.scanner.nextLine().toUpperCase();
      if ("".equals(scannedItem)) {
        break;
      }

      try {
        cartService.addItem(scannedItem);
      } catch (ItemNotFoundException e) {
        this.out.println("Sorry I wasn't able to find that product");
        continue;
      }

      this.out.printf("Running total -- %s%n", convertToGBP(cartService.getRunningTotal()));
    } while (true);
  }

  private void outputSummary(CartService cartService) {
    this.out.println("The summary of items scanned are as follows");
    this.out.println();

    this.out.println("-----Scanned Items-----");
    cartService.getScannedItems().forEach((key, quantity) -> this.out.printf("%d x %s%n", quantity, key));
    this.out.println();

    this.out.println("-----Breakdown-----");
    this.out.printf("Total (prior to discounts) = %s%n", convertToGBP(cartService.getRunningTotal()));
    this.out.printf("Discounts = %s%n", convertToGBP(cartService.getTotalDiscount()));
    this.out.printf("Total to pay = %s%n", convertToGBP(cartService.getRunningTotal() - cartService.getTotalDiscount()));
  }

  private int collectNumericInput(InputCondition<Integer> inputCondition, String rePromptMessage) {
    int temp;
    try {
      temp = this.scanner.nextInt();
    } catch (InputMismatchException e) {
      this.scanner.nextLine(); // To prevent recursive loop by expecting user to input new value

      this.out.printf("Invalid input supplied. %s%n", rePromptMessage);
      return this.collectNumericInput(inputCondition, rePromptMessage);
    }

    this.scanner.nextLine();
    if (inputCondition.valid(temp)) {
      return temp;
    }
    this.out.println(rePromptMessage);
    return this.collectNumericInput(inputCondition, rePromptMessage);
  }

  private String collectYOrNInput(String rePromptMessage) {
    return this.collectInput((String temp) -> Arrays.asList(new String[]{"Y", "N"}).contains(temp), rePromptMessage);
  }

  private String collectInput(InputCondition<String> inputCondition, String rePromptMessage) {
    final String temp = this.scanner.nextLine().toUpperCase();

    if (inputCondition.valid(temp)) {
      return temp;
    }
    this.out.println(rePromptMessage);
    return this.collectInput(inputCondition, rePromptMessage);
  }

  private String convertToGBP(int amountInPence) {
    final NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale.Builder().setLanguage("en").setRegion("GB").build());
    return formatter.format((double) amountInPence / 100);
  }
}

interface InputCondition<T> {
  boolean valid(T input);
}