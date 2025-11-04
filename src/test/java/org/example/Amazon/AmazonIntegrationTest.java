package org.example.Amazon;

import org.example.Amazon.Cost.PriceRule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AmazonIntegrationTest {

    private ShoppingCart cart;
    private List<PriceRule> rules;
    private Amazon amazon;

    @BeforeEach
    void setUp() {
        cart = mock(ShoppingCart.class);
        rules = new ArrayList<>();

        PriceRule mockRule1 = mock(PriceRule.class);
        PriceRule mockRule2 = mock(PriceRule.class);

        when(mockRule1.priceToAggregate(cart.getItems())).thenReturn(200.0);
        when(mockRule2.priceToAggregate(cart.getItems())).thenReturn(300.0);

        rules.add(mockRule1);
        rules.add(mockRule2);

        amazon = new Amazon(cart, rules);
    }

    @Test
    @DisplayName("Specification-based: calculate() adds totals from all PriceRules")
    void testCalculate_SpecificationBased() {
        double total = amazon.calculate();
        assertEquals(500.0, total, 0.01, "Total should equal the sum of all PriceRule results");
    }

    @Test
    @DisplayName("Structural-based: addToCart() delegates to ShoppingCart.add()")
    void testAddToCart_StructuralBased() {
        Item item = mock(Item.class);
        amazon.addToCart(item);

        verify(cart, times(1)).add(item);
    }
}