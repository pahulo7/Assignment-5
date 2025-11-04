package org.example.Barnes;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class BarnesAndNobleIntegrationTest {

    private BarnesAndNoble barnesAndNoble;

    static class FakeBookDatabase implements BookDatabase {
        @Override
        public Book findByISBN(String isbn) {
            if (isbn.equals("101")) return new Book("101", 50, 3);
            if (isbn.equals("102")) return new Book("102", 25, 5);
            // return a placeholder instead of null
            return new Book(isbn, 0, 0);
        }
    }

    static class FakeBuyProcess implements BuyBookProcess {
        private int totalBought = 0;

        @Override
        public void buyBook(Book book, int quantity) {
            totalBought += quantity;
        }

        public int getTotalBought() {
            return totalBought;
        }
    }

    private FakeBuyProcess fakeProcess;

    @BeforeEach
    void setUp() {
        fakeProcess = new FakeBuyProcess();
        barnesAndNoble = new BarnesAndNoble(new FakeBookDatabase(), fakeProcess);
    }

    @Test
    void testRegularPurchaseFlow() {
        Map<String, Integer> order = new HashMap<>();
        order.put("101", 2);

        PurchaseSummary summary = barnesAndNoble.getPriceForCart(order);

        assertThat(summary.getTotalPrice()).isEqualTo(100);
        assertThat(fakeProcess.getTotalBought()).isEqualTo(2);
    }

    @Test
    void testPartiallyUnavailableBook() {
        Map<String, Integer> order = new HashMap<>();
        order.put("101", 5); // only 3 available

        PurchaseSummary summary = barnesAndNoble.getPriceForCart(order);

        assertThat(summary.getTotalPrice()).isEqualTo(150);
        assertThat(summary.getUnavailable()).isNotEmpty();
    }

    @Test
    void testBookNotFoundDoesNotCrash() {
        Map<String, Integer> order = new HashMap<>();
        order.put("404", 1);

        PurchaseSummary summary = barnesAndNoble.getPriceForCart(order);

        assertThat(summary.getTotalPrice()).isEqualTo(0);
        assertThat(summary.getUnavailable()).isNotEmpty();
    }
}

