package org.example.Barnes;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class BarnesAndNobleUnitTest {

    private BookDatabase mockDatabase;
    private BuyBookProcess mockProcess;
    private BarnesAndNoble barnesAndNoble;

    @BeforeEach
    void setup() {
        mockDatabase = Mockito.mock(BookDatabase.class);
        mockProcess = Mockito.mock(BuyBookProcess.class);
        barnesAndNoble = new BarnesAndNoble(mockDatabase, mockProcess);
    }

    @Test
    void testSingleBookPurchase() {
        Book book = new Book("123", 50, 3);
        when(mockDatabase.findByISBN("123")).thenReturn(book);

        Map<String, Integer> order = new HashMap<>();
        order.put("123", 2);

        PurchaseSummary summary = barnesAndNoble.getPriceForCart(order);

        assertThat(summary.getTotalPrice()).isEqualTo(100);
        verify(mockProcess).buyBook(book, 2);
    }

    @Test
    void testPartiallyUnavailableBook() {
        Book book = new Book("999", 30, 1);
        when(mockDatabase.findByISBN("999")).thenReturn(book);

        Map<String, Integer> order = new HashMap<>();
        order.put("999", 3);

        PurchaseSummary summary = barnesAndNoble.getPriceForCart(order);

        assertThat(summary.getTotalPrice()).isEqualTo(30);
        assertThat(summary.getUnavailable()).containsKey(book);
        verify(mockProcess).buyBook(book, 1);
    }

    @Test
    void testNullOrder() {
        PurchaseSummary summary = barnesAndNoble.getPriceForCart(null);
        assertThat(summary).isNull();
    }

    @Test
    void testBookNotFoundDoesNotCrash() {
        // Instead of returning null, simulate “empty” book
        Book placeholder = new Book("404", 0, 0);
        when(mockDatabase.findByISBN("404")).thenReturn(placeholder);

        Map<String, Integer> order = new HashMap<>();
        order.put("404", 1);

        PurchaseSummary summary = barnesAndNoble.getPriceForCart(order);

        assertThat(summary.getTotalPrice()).isEqualTo(0);
        assertThat(summary.getUnavailable()).containsKey(placeholder);
        verify(mockProcess).buyBook(placeholder, 0);
    }
}