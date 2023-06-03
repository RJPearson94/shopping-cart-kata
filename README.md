# Shopping Cart Kata

This Java command line application simulates a checkout system. 

Before items can be scanned, the user must configure 1 or more Stock Keeping Units (SKUs) when prompted, filling in the price and conditionally configuring promotional pricing.

Example pricing can be seen below 

| Item | Unit Price (pence) | Promotional Pricing (pence) |
|------|--------------------|-----------------------------|
| A    | 50                 | 3 for 130                   | 
| B    | 30                 | 2 for 45                    |
| C    | 20                 |                             |
| D    | 15                 |                             | 

## Design decisions

The solution is influenced by a number of design decision that have been made. Some of these are documented below:

- Lombok is used to reduce the amount of boilerplate
- SKU IDs are stored in uppercase, this is to prevent both upper and lowercase character being used to represent different items.
- SKU IDs are string, this is to allow more than 26 items to be stored.
- SKUs cannot be modified/ overridden after they have been configured
- The unit price, promotional quantity and promotional pricing must be greater than 0.
- The promotional price cannot be greater or equal to the full price of the item i.e. if an item costs 50p, the promotional cost of 3 cannot be greater or equal to £1.50
- If an invalid input is supplied the user is re-prompted for a valid input
- If an invalid/ unknown item is scanned. The user is re-prompted and the item is not tracked
- Once an item has been scanned, it cannot be removed
- Based upon the initial requirements, each item must be scanned. A quantity cannot be supplied when scanning items
- A user can end a checkout session without scanning an item

## Getting Started

To application requires Java 17 (latest LTS at the time of writing) or above

The application uses gradle to build the application and manage dependencies. To reduce the amount of setup that is required a Gradle wrapper script is supplied to download and configure the version of gradle that the application was developed with.

### Building the application

To build the application, please run the following command

```shell
./gradlew build
```

### Running the unit test

The application has a number of unit test which test various scenarios. To run the test, please run the following command

```shell
./gradlew test
```

### Running the application

To launch the application, please run the following command

```shell
./gradlew run
```

Then please answer the questions when prompted