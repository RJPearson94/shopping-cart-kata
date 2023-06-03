package shopping.cart.kata.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import shopping.cart.kata.models.Sku;
import shopping.cart.kata.models.SkuSpecialPricing;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CheckoutServiceTests {

  @Test
  public void shouldConfigureOneSkuWithoutSpecialPricing() {
    // Given
    final String[] userInput = new String[]{
        "C",
        "20",
        "N",
        "N"
    };
    final InputStream inputStream = new ByteArrayInputStream(String.join("\n", userInput).getBytes());
    final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    final PrintStream printStream = new PrintStream(byteArrayOutputStream);
    final CheckoutServiceTestSubject testSubject = new CheckoutServiceTestSubject(inputStream, printStream);

    // When
    final Map<String, Sku> skus = testSubject.configureSkus();

    // Then
    final List<String> expectedResponse = new ArrayList<>();
    expectedResponse.add("Before we start I need you to configure the SKUs");
    expectedResponse.add("");
    expectedResponse.addAll(Arrays.asList(this.skuWithoutSpecialPricingInput()));
    assertEquals(String.join("\n", expectedResponse), byteArrayOutputStream.toString().trim());

    assertEquals(1, skus.keySet().size());
    assertSkuWithoutSpecialPricing(skus, "C", 20);
  }

  @Test
  public void shouldConfigureOneSkuWithSpecialPricing() {
    // Given
    final String[] userInput = new String[]{
        "A",
        "50",
        "Y",
        "3",
        "130",
        "N",
    };
    final InputStream inputStream = new ByteArrayInputStream(String.join("\n", userInput).getBytes());
    final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    final PrintStream printStream = new PrintStream(byteArrayOutputStream);
    final CheckoutServiceTestSubject testSubject = new CheckoutServiceTestSubject(inputStream, printStream);

    // When
    final Map<String, Sku> skus = testSubject.configureSkus();

    // Then
    final List<String> expectedResponse = new ArrayList<>();
    expectedResponse.add("Before we start I need you to configure the SKUs");
    expectedResponse.add("");
    expectedResponse.addAll(Arrays.asList(this.skuWithSpecialPricingInput()));
    assertEquals(String.join("\n", expectedResponse), byteArrayOutputStream.toString().trim());

    assertEquals(1, skus.keySet().size());
    assertSkuWithSpecialPricing(skus, "A", 50, 3, 130, 20);
  }

  @Test
  public void shouldConfigureMultipleSkus() {
    // Given
    final String[] userInput = new String[]{
        "A",
        "50",
        "Y",
        "3",
        "130",
        "Y",
        "B",
        "30",
        "Y",
        "2",
        "45",
        "Y",
        "C",
        "20",
        "N",
        "Y",
        "D",
        "15",
        "N",
        "N"
    };
    final InputStream inputStream = new ByteArrayInputStream(String.join("\n", userInput).getBytes());
    final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    final PrintStream printStream = new PrintStream(byteArrayOutputStream);
    final CheckoutServiceTestSubject testSubject = new CheckoutServiceTestSubject(inputStream, printStream);

    // When
    final Map<String, Sku> skus = testSubject.configureSkus();

    // Then
    final List<String> expectedResponse = new ArrayList<>();
    expectedResponse.add("Before we start I need you to configure the SKUs");
    expectedResponse.add("");
    expectedResponse.addAll(Arrays.asList(this.skuWithSpecialPricingInput()));
    expectedResponse.addAll(Arrays.asList(this.skuWithSpecialPricingInput()));
    expectedResponse.addAll(Arrays.asList(this.skuWithoutSpecialPricingInput()));
    expectedResponse.addAll(Arrays.asList(this.skuWithoutSpecialPricingInput()));
    assertEquals(String.join("\n", expectedResponse), byteArrayOutputStream.toString().trim());

    assertEquals(4, skus.keySet().size());
    assertSkuWithSpecialPricing(skus, "A", 50, 3, 130, 20);
    assertSkuWithSpecialPricing(skus, "B", 30, 2, 45, 15);
    assertSkuWithoutSpecialPricing(skus, "C", 20);
    assertSkuWithoutSpecialPricing(skus, "D", 15);
  }

  @Test
  public void shouldRePromptWhenTheItemPriceIs0() {
    // Given
    final String[] userInput = new String[]{
        "C",
        "0",
        "20",
        "N",
        "N"
    };
    final InputStream inputStream = new ByteArrayInputStream(String.join("\n", userInput).getBytes());
    final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    final PrintStream printStream = new PrintStream(byteArrayOutputStream);
    final CheckoutServiceTestSubject testSubject = new CheckoutServiceTestSubject(inputStream, printStream);

    // When
    final Map<String, Sku> skus = testSubject.configureSkus();

    // Then
    final String[] expectedResponse = new String[]{
        "Before we start I need you to configure the SKUs",
        "",
        "Please enter the ID of the item",
        "Please enter the price of the item (in pence)",
        "Please enter the price of the item (in pence). The value must be greater than 0",
        "Does the item have promotional pricing?. Please enter Y or N",
        "I have stored that item. Would you like to configure any more SKUs? Please enter Y or N"
    };
    assertEquals(String.join("\n", expectedResponse), byteArrayOutputStream.toString().trim());

    assertEquals(1, skus.keySet().size());
    assertSkuWithoutSpecialPricing(skus, "C", 20);
  }

  @Test
  public void shouldRePromptWhenANonNumericValueIsSuppliedForThePrice() {
    // Given
    final String[] userInput = new String[]{
        "D",
        "D",
        "15",
        "N",
        "N"
    };
    final InputStream inputStream = new ByteArrayInputStream(String.join("\n", userInput).getBytes());
    final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    final PrintStream printStream = new PrintStream(byteArrayOutputStream);
    final CheckoutServiceTestSubject testSubject = new CheckoutServiceTestSubject(inputStream, printStream);

    // When
    final Map<String, Sku> skus = testSubject.configureSkus();

    // Then
    final String[] expectedResponse = new String[]{
        "Before we start I need you to configure the SKUs",
        "",
        "Please enter the ID of the item",
        "Please enter the price of the item (in pence)",
        "Invalid input supplied. Please enter the price of the item (in pence). The value must be greater than 0",
        "Does the item have promotional pricing?. Please enter Y or N",
        "I have stored that item. Would you like to configure any more SKUs? Please enter Y or N"
    };
    assertEquals(String.join("\n", expectedResponse), byteArrayOutputStream.toString().trim());

    assertEquals(1, skus.keySet().size());
    assertSkuWithoutSpecialPricing(skus, "D", 15);
  }

  @Test
  public void shouldRePromptWhenTheQuantityIsZero() {
    // Given
    final String[] userInput = new String[]{
        "B",
        "30",
        "Y",
        "0",
        "2",
        "45",
        "N",
    };
    final InputStream inputStream = new ByteArrayInputStream(String.join("\n", userInput).getBytes());
    final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    final PrintStream printStream = new PrintStream(byteArrayOutputStream);
    final CheckoutServiceTestSubject testSubject = new CheckoutServiceTestSubject(inputStream, printStream);

    // When
    final Map<String, Sku> skus = testSubject.configureSkus();

    // Then
    final String[] expectedResponse = new String[]{
        "Before we start I need you to configure the SKUs",
        "",
        "Please enter the ID of the item",
        "Please enter the price of the item (in pence)",
        "Does the item have promotional pricing?. Please enter Y or N",
        "Please enter the number of items required to qualify for the discount",
        "Please enter the number of items required to qualify for the discount. The value must be greater than 0",
        "Please enter the promotional price (in pence)",
        "I have stored that item. Would you like to configure any more SKUs? Please enter Y or N"
    };
    assertEquals(String.join("\n", expectedResponse), byteArrayOutputStream.toString().trim());

    assertEquals(1, skus.keySet().size());
    assertSkuWithSpecialPricing(skus, "B", 30, 2, 45, 15);
  }

  @Test
  public void shouldRePromptWhenANonNumericValueIsSuppliedForTheQuantity() {
    // Given
    final String[] userInput = new String[]{
        "B",
        "30",
        "Y",
        "A",
        "2",
        "45",
        "N",
    };
    final InputStream inputStream = new ByteArrayInputStream(String.join("\n", userInput).getBytes());
    final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    final PrintStream printStream = new PrintStream(byteArrayOutputStream);
    final CheckoutServiceTestSubject testSubject = new CheckoutServiceTestSubject(inputStream, printStream);

    // When
    final Map<String, Sku> skus = testSubject.configureSkus();

    // Then
    final String[] expectedResponse = new String[]{
        "Before we start I need you to configure the SKUs",
        "",
        "Please enter the ID of the item",
        "Please enter the price of the item (in pence)",
        "Does the item have promotional pricing?. Please enter Y or N",
        "Please enter the number of items required to qualify for the discount",
        "Invalid input supplied. Please enter the number of items required to qualify for the discount. The value must be greater than 0",
        "Please enter the promotional price (in pence)",
        "I have stored that item. Would you like to configure any more SKUs? Please enter Y or N"
    };
    assertEquals(String.join("\n", expectedResponse), byteArrayOutputStream.toString().trim());

    assertEquals(1, skus.keySet().size());
    assertSkuWithSpecialPricing(skus, "B", 30, 2, 45, 15);
  }

  @Test
  public void shouldRePromptWhenThePromotionalPriceIsZero() {
    // Given
    final String[] userInput = new String[]{
        "B",
        "30",
        "Y",
        "2",
        "0",
        "45",
        "N",
    };
    final InputStream inputStream = new ByteArrayInputStream(String.join("\n", userInput).getBytes());
    final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    final PrintStream printStream = new PrintStream(byteArrayOutputStream);
    final CheckoutServiceTestSubject testSubject = new CheckoutServiceTestSubject(inputStream, printStream);

    // When
    final Map<String, Sku> skus = testSubject.configureSkus();

    // Then
    final String[] expectedResponse = new String[]{
        "Before we start I need you to configure the SKUs",
        "",
        "Please enter the ID of the item",
        "Please enter the price of the item (in pence)",
        "Does the item have promotional pricing?. Please enter Y or N",
        "Please enter the number of items required to qualify for the discount",
        "Please enter the promotional price (in pence)",
        "Please enter the promotional price (in pence). The price must be greater than 0 and less than the full price of all items",
        "I have stored that item. Would you like to configure any more SKUs? Please enter Y or N"
    };
    assertEquals(String.join("\n", expectedResponse), byteArrayOutputStream.toString().trim());

    assertEquals(1, skus.keySet().size());
    assertSkuWithSpecialPricing(skus, "B", 30, 2, 45, 15);
  }

  @Test
  public void shouldRePromptWhenANonNumericValueIsSuppliedForThePromotionalPrice() {
    // Given
    final String[] userInput = new String[]{
        "B",
        "30",
        "Y",
        "2",
        "A",
        "45",
        "N",
    };
    final InputStream inputStream = new ByteArrayInputStream(String.join("\n", userInput).getBytes());
    final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    final PrintStream printStream = new PrintStream(byteArrayOutputStream);
    final CheckoutServiceTestSubject testSubject = new CheckoutServiceTestSubject(inputStream, printStream);

    // When
    final Map<String, Sku> skus = testSubject.configureSkus();

    // Then
    final String[] expectedResponse = new String[]{
        "Before we start I need you to configure the SKUs",
        "",
        "Please enter the ID of the item",
        "Please enter the price of the item (in pence)",
        "Does the item have promotional pricing?. Please enter Y or N",
        "Please enter the number of items required to qualify for the discount",
        "Please enter the promotional price (in pence)",
        "Invalid input supplied. Please enter the promotional price (in pence). The price must be greater than 0 and less than the full price of all items",
        "I have stored that item. Would you like to configure any more SKUs? Please enter Y or N"
    };
    assertEquals(String.join("\n", expectedResponse), byteArrayOutputStream.toString().trim());

    assertEquals(1, skus.keySet().size());
    assertSkuWithSpecialPricing(skus, "B", 30, 2, 45, 15);
  }

  @Test
  public void shouldRePromptWhenThePromotionalPriceIsEqualToTheFullPrice() {
    // Given
    final String[] userInput = new String[]{
        "B",
        "30",
        "Y",
        "2",
        "60",
        "45",
        "N",
    };
    final InputStream inputStream = new ByteArrayInputStream(String.join("\n", userInput).getBytes());
    final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    final PrintStream printStream = new PrintStream(byteArrayOutputStream);
    final CheckoutServiceTestSubject testSubject = new CheckoutServiceTestSubject(inputStream, printStream);

    // When
    final Map<String, Sku> skus = testSubject.configureSkus();

    // Then
    final String[] expectedResponse = new String[]{
        "Before we start I need you to configure the SKUs",
        "",
        "Please enter the ID of the item",
        "Please enter the price of the item (in pence)",
        "Does the item have promotional pricing?. Please enter Y or N",
        "Please enter the number of items required to qualify for the discount",
        "Please enter the promotional price (in pence)",
        "Please enter the promotional price (in pence). The price must be greater than 0 and less than the full price of all items",
        "I have stored that item. Would you like to configure any more SKUs? Please enter Y or N"
    };
    assertEquals(String.join("\n", expectedResponse), byteArrayOutputStream.toString().trim());

    assertEquals(1, skus.keySet().size());
    assertSkuWithSpecialPricing(skus, "B", 30, 2, 45, 15);
  }

  @Test
  public void shouldRePromptWhenZIsEnteredForThePromotionalPricingQuestion() {
    // Given
    final String[] userInput = new String[]{
        "C",
        "20",
        "Z",
        "N",
        "N"
    };
    final InputStream inputStream = new ByteArrayInputStream(String.join("\n", userInput).getBytes());
    final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    final PrintStream printStream = new PrintStream(byteArrayOutputStream);
    final CheckoutServiceTestSubject testSubject = new CheckoutServiceTestSubject(inputStream, printStream);

    // When
    final Map<String, Sku> skus = testSubject.configureSkus();

    // Then
    final String[] expectedResponse = new String[]{
        "Before we start I need you to configure the SKUs",
        "",
        "Please enter the ID of the item",
        "Please enter the price of the item (in pence)",
        "Does the item have promotional pricing?. Please enter Y or N",
        "Does the item have promotional pricing?. Please enter Y or N",
        "I have stored that item. Would you like to configure any more SKUs? Please enter Y or N"
    };
    assertEquals(String.join("\n", expectedResponse), byteArrayOutputStream.toString().trim());

    assertEquals(1, skus.keySet().size());
    assertSkuWithoutSpecialPricing(skus, "C", 20);
  }

  @Test
  public void shouldRePromptWhenZIsEnteredForTheConfigureAdditionalSkusQuestion() {
    // Given
    final String[] userInput = new String[]{
        "C",
        "20",
        "N",
        "Z",
        "N"
    };
    final InputStream inputStream = new ByteArrayInputStream(String.join("\n", userInput).getBytes());
    final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    final PrintStream printStream = new PrintStream(byteArrayOutputStream);
    final CheckoutServiceTestSubject testSubject = new CheckoutServiceTestSubject(inputStream, printStream);

    // When
    final Map<String, Sku> skus = testSubject.configureSkus();

    // Then
    final String[] expectedResponse = new String[]{
        "Before we start I need you to configure the SKUs",
        "",
        "Please enter the ID of the item",
        "Please enter the price of the item (in pence)",
        "Does the item have promotional pricing?. Please enter Y or N",
        "I have stored that item. Would you like to configure any more SKUs? Please enter Y or N",
        "Would you like to configure any more SKUs? Please enter Y or N"
    };
    assertEquals(String.join("\n", expectedResponse), byteArrayOutputStream.toString().trim());

    assertEquals(1, skus.keySet().size());
    assertSkuWithoutSpecialPricing(skus, "C", 20);
  }

  @Test
  public void shouldRePromptWhenDuplicateIdIsSupplied() {
    // Given
    final String[] userInput = new String[]{
        "C",
        "20",
        "N",
        "Y",
        "c",
        "d",
        "15",
        "N",
        "N",
    };
    final InputStream inputStream = new ByteArrayInputStream(String.join("\n", userInput).getBytes());
    final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    final PrintStream printStream = new PrintStream(byteArrayOutputStream);
    final CheckoutServiceTestSubject testSubject = new CheckoutServiceTestSubject(inputStream, printStream);

    // When
    final Map<String, Sku> skus = testSubject.configureSkus();

    // Then
    final String[] expectedResponse = new String[]{
        "Before we start I need you to configure the SKUs",
        "",
        "Please enter the ID of the item",
        "Please enter the price of the item (in pence)",
        "Does the item have promotional pricing?. Please enter Y or N",
        "I have stored that item. Would you like to configure any more SKUs? Please enter Y or N",
        "Please enter the ID of the item",
        "The ID supplied has already been configured. Please enter a unique ID of the item",
        "Please enter the price of the item (in pence)",
        "Does the item have promotional pricing?. Please enter Y or N",
        "I have stored that item. Would you like to configure any more SKUs? Please enter Y or N",
    };
    assertEquals(String.join("\n", expectedResponse), byteArrayOutputStream.toString().trim());

    assertEquals(2, skus.keySet().size());
    assertSkuWithoutSpecialPricing(skus, "C", 20);
    assertSkuWithoutSpecialPricing(skus, "D", 15);
  }

  @Test
  public void shouldScanOneItemAndDisplayRunningTotal() {
    // Given
    final String[] userInput = new String[]{
        "A",
        "\n"
    };
    final InputStream inputStream = new ByteArrayInputStream(String.join("\n", userInput).getBytes());
    final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    final PrintStream printStream = new PrintStream(byteArrayOutputStream);
    final CheckoutServiceTestSubject testSubject = new CheckoutServiceTestSubject(inputStream, printStream);

    // When
    final CartService cartService = this.configuredCartService();
    testSubject.scanItems(cartService);

    // Then
    final String[] expectedResponse = new String[]{
        "SKUs have been configured. Please proceed to scanning items",
        "",
        "Please scan your next item. Press enter when all items have been scanned",
        "Running total -- £0.50",
        "Please scan your next item. Press enter when all items have been scanned"
    };
    assertEquals(String.join("\n", expectedResponse), byteArrayOutputStream.toString().trim());
  }

  @Test
  public void shouldScanTwoDifferentItemsAndDisplayRunningTotal() {
    // Given
    final String[] userInput = new String[]{
        "A",
        "B",
        "\n"
    };
    final InputStream inputStream = new ByteArrayInputStream(String.join("\n", userInput).getBytes());
    final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    final PrintStream printStream = new PrintStream(byteArrayOutputStream);
    final CheckoutServiceTestSubject testSubject = new CheckoutServiceTestSubject(inputStream, printStream);

    // When
    final CartService cartService = this.configuredCartService();
    testSubject.scanItems(cartService);

    // Then
    final String[] expectedResponse = new String[]{
        "SKUs have been configured. Please proceed to scanning items",
        "",
        "Please scan your next item. Press enter when all items have been scanned",
        "Running total -- £0.50",
        "Please scan your next item. Press enter when all items have been scanned",
        "Running total -- £0.80",
        "Please scan your next item. Press enter when all items have been scanned"
    };
    assertEquals(String.join("\n", expectedResponse), byteArrayOutputStream.toString().trim());
  }

  @Test
  public void shouldScanTheSameItemMultipleTimesToGetADiscount() {
    // Given
    final String[] userInput = new String[]{
        "A",
        "A",
        "A",
        "\n"
    };
    final InputStream inputStream = new ByteArrayInputStream(String.join("\n", userInput).getBytes());
    final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    final PrintStream printStream = new PrintStream(byteArrayOutputStream);
    final CheckoutServiceTestSubject testSubject = new CheckoutServiceTestSubject(inputStream, printStream);

    // When
    final CartService cartService = this.configuredCartService();
    testSubject.scanItems(cartService);

    // Then
    final String[] expectedResponse = new String[]{
        "SKUs have been configured. Please proceed to scanning items",
        "",
        "Please scan your next item. Press enter when all items have been scanned",
        "Running total -- £0.50",
        "Please scan your next item. Press enter when all items have been scanned",
        "Running total -- £1.00",
        "Please scan your next item. Press enter when all items have been scanned",
        "Running total -- £1.50",
        "Please scan your next item. Press enter when all items have been scanned"
    };
    assertEquals(String.join("\n", expectedResponse), byteArrayOutputStream.toString().trim());
  }

  @Test
  public void shouldShowAMessageWhenAnInvalidItemIsScanned() {
    // Given
    final String[] userInput = new String[]{
        "Z",
        "\n"
    };
    final InputStream inputStream = new ByteArrayInputStream(String.join("\n", userInput).getBytes());
    final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    final PrintStream printStream = new PrintStream(byteArrayOutputStream);
    final CheckoutServiceTestSubject testSubject = new CheckoutServiceTestSubject(inputStream, printStream);

    // When
    final CartService cartService = this.configuredCartService();
    testSubject.scanItems(cartService);

    // Then
    final String[] expectedResponse = new String[]{
        "SKUs have been configured. Please proceed to scanning items",
        "",
        "Please scan your next item. Press enter when all items have been scanned",
        "Sorry I wasn't able to find that product",
        "Please scan your next item. Press enter when all items have been scanned"
    };
    assertEquals(String.join("\n", expectedResponse), byteArrayOutputStream.toString().trim());
  }

  @Test
  public void configureOneSkuWithSpecialPricingAndScanZeroItems() {
    // Given
    final String[] userInput = new String[]{
        "A",
        "50",
        "Y",
        "3",
        "130",
        "N",

        "\n"
    };
    final InputStream inputStream = new ByteArrayInputStream(String.join("\n", userInput).getBytes());
    final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    final PrintStream printStream = new PrintStream(byteArrayOutputStream);
    final CheckoutServiceTestSubject testSubject = new CheckoutServiceTestSubject(inputStream, printStream);

    // When
    testSubject.run();

    // Then
    final String[] expectedResponse = new String[]{
        "Welcome to kata shopping basket by Rob Pearson",
        "",
        "Before we start I need you to configure the SKUs",
        "",
        "Please enter the ID of the item",
        "Please enter the price of the item (in pence)",
        "Does the item have promotional pricing?. Please enter Y or N",
        "Please enter the number of items required to qualify for the discount",
        "Please enter the promotional price (in pence)",
        "I have stored that item. Would you like to configure any more SKUs? Please enter Y or N",
        "SKUs have been configured. Please proceed to scanning items",
        "",
        "Please scan your next item. Press enter when all items have been scanned",
        "The summary of items scanned are as follows",
        "",
        "-----Scanned Items-----",
        "",
        "-----Breakdown-----",
        "Total (prior to discounts) = £0.00",
        "Discounts = £0.00",
        "Total to pay = £0.00"
    };
    assertEquals(String.join("\n", expectedResponse), byteArrayOutputStream.toString().trim());
  }

  @Test
  public void configureConfigureAndScanFourItems() {
    // Given
    final String[] userInput = new String[]{
        "A",
        "50",
        "Y",
        "3",
        "130",
        "Y",
        "B",
        "30",
        "Y",
        "2",
        "45",
        "Y",
        "C",
        "20",
        "N",
        "Y",
        "D",
        "15",
        "N",
        "N",

        "A",
        "A",
        "B",
        "A",
        "B",
        "C",
        "D",
        "\n"
    };
    final InputStream inputStream = new ByteArrayInputStream(String.join("\n", userInput).getBytes());
    final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    final PrintStream printStream = new PrintStream(byteArrayOutputStream);
    final CheckoutServiceTestSubject testSubject = new CheckoutServiceTestSubject(inputStream, printStream);

    // When
    testSubject.run();

    // Then
    final String[] expectedResponse = new String[]{
        "Welcome to kata shopping basket by Rob Pearson",
        "",
        "Before we start I need you to configure the SKUs",
        "",
        "Please enter the ID of the item",
        "Please enter the price of the item (in pence)",
        "Does the item have promotional pricing?. Please enter Y or N",
        "Please enter the number of items required to qualify for the discount",
        "Please enter the promotional price (in pence)",
        "I have stored that item. Would you like to configure any more SKUs? Please enter Y or N",
        "Please enter the ID of the item",
        "Please enter the price of the item (in pence)",
        "Does the item have promotional pricing?. Please enter Y or N",
        "Please enter the number of items required to qualify for the discount",
        "Please enter the promotional price (in pence)",
        "I have stored that item. Would you like to configure any more SKUs? Please enter Y or N",
        "Please enter the ID of the item",
        "Please enter the price of the item (in pence)",
        "Does the item have promotional pricing?. Please enter Y or N",
        "I have stored that item. Would you like to configure any more SKUs? Please enter Y or N",
        "Please enter the ID of the item",
        "Please enter the price of the item (in pence)",
        "Does the item have promotional pricing?. Please enter Y or N",
        "I have stored that item. Would you like to configure any more SKUs? Please enter Y or N",
        "SKUs have been configured. Please proceed to scanning items",
        "",
        "Please scan your next item. Press enter when all items have been scanned",
        "Running total -- £0.50",
        "Please scan your next item. Press enter when all items have been scanned",
        "Running total -- £1.00",
        "Please scan your next item. Press enter when all items have been scanned",
        "Running total -- £1.30",
        "Please scan your next item. Press enter when all items have been scanned",
        "Running total -- £1.80",
        "Please scan your next item. Press enter when all items have been scanned",
        "Running total -- £2.10",
        "Please scan your next item. Press enter when all items have been scanned",
        "Running total -- £2.30",
        "Please scan your next item. Press enter when all items have been scanned",
        "Running total -- £2.45",
        "Please scan your next item. Press enter when all items have been scanned",
        "The summary of items scanned are as follows",
        "",
        "-----Scanned Items-----",
        "3 x A",
        "2 x B",
        "1 x C",
        "1 x D",
        "",
        "-----Breakdown-----",
        "Total (prior to discounts) = £2.45",
        "Discounts = £0.35",
        "Total to pay = £2.10"
    };
    assertEquals(String.join("\n", expectedResponse), byteArrayOutputStream.toString().trim());
  }
  private String[] skuWithoutSpecialPricingInput() {
    return new String[] {
        "Please enter the ID of the item",
        "Please enter the price of the item (in pence)",
        "Does the item have promotional pricing?. Please enter Y or N",
        "I have stored that item. Would you like to configure any more SKUs? Please enter Y or N"
    };
  }

  private String[] skuWithSpecialPricingInput() {
    return new String[] {
        "Please enter the ID of the item",
        "Please enter the price of the item (in pence)",
        "Does the item have promotional pricing?. Please enter Y or N",
        "Please enter the number of items required to qualify for the discount",
        "Please enter the promotional price (in pence)",
        "I have stored that item. Would you like to configure any more SKUs? Please enter Y or N",
    };
  }

  private void assertSkuWithSpecialPricing(Map<String, Sku> skus, String id, int price, int quantity, int overriddenPrice, int priceDiscount) {
    assertTrue(skus.containsKey(id));

    final Sku configuredSku = skus.get(id);
    assertEquals(price, configuredSku.getPrice());

    final SkuSpecialPricing configuredSkuSpecialPricing = configuredSku.getSkuSpecialPricing();
    assertNotNull(configuredSkuSpecialPricing);

    assertEquals(quantity, configuredSkuSpecialPricing.getQuantity());
    assertEquals(overriddenPrice, configuredSkuSpecialPricing.getOverriddenPrice());
    assertEquals(priceDiscount, configuredSkuSpecialPricing.getPriceDiscount());
  }

  private void assertSkuWithoutSpecialPricing(Map<String, Sku> skus, String id, int price) {
    assertTrue(skus.containsKey(id));

    final Sku configuredSku = skus.get(id);
    assertEquals(price, configuredSku.getPrice());

    final SkuSpecialPricing configuredSkuSpecialPricing = configuredSku.getSkuSpecialPricing();
    assertNull(configuredSkuSpecialPricing);
  }

  private CartService configuredCartService() {
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

    return new CartService(skus);
  }
}

class CheckoutServiceTestSubject extends CheckoutService {

  public CheckoutServiceTestSubject(InputStream in, PrintStream out) {
    super(in, out);
  }

  public Map<String, Sku> configureSkus() {
    return super.configureSkus();
  }

  public void scanItems(CartService cartService) {
    super.scanItems(cartService);
  }
}