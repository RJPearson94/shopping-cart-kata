package shopping.cart.kata;

import shopping.cart.kata.services.CheckoutService;

public class App {

    public static void main(String[] args) {
        final CheckoutService checkoutService = new CheckoutService(System.in, System.out);
        checkoutService.run();
    }
}
