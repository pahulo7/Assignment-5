package org.example.Amazon;

import org.example.Amazon.Cost.PriceRule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AmazonUnitTest {

    private ShoppingCart mockCart;
    private List<PriceRule> mockRules;
    private Amazon amazon;

    @BeforeEach
    void setUp() {
        mockCart = mock(ShoppingCart.class);
        mockRules = List.of(mock(PriceRule.class));
        amazon = new Amazon(mockCart, mockRules);
    }

    @Test
    @DisplayName("Specification-based: calculate() sums price from all rules")
    void testCalculate_SpecificationBased() {
        when(mockRules.get(0).priceToAggregate(mockCart.getItems())).thenReturn(500.0);

        double total = amazon.calculate();

        assertEquals(500.0, total, 0.01, "Total should equal the mock rule’s calculated value");
        verify(mockRules.get(0), times(1)).priceToAggregate(mockCart.getItems());
    }

    @Test
    @DisplayName("Structural-based: calculate() returns 0 when there are no rules")
    void testCalculate_StructuralBased() {
        amazon = new Amazon(mockCart, List.of()); // empty rules list
        double total = amazon.calculate();

        assertEquals(0.0, total, 0.01, "Total should be 0 when there are no rules");
    }
}