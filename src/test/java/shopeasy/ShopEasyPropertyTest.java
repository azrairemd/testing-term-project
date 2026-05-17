package shopeasy;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Combinators;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;
import net.jqwik.api.constraints.DoubleRange;

/**
 * Task 4 – Property-Based Testing (Chapter 5)
 *
 * <p>Target classes: {@link PriceCalculator}, {@link ShoppingCart}
 *
 * <p>Using jqwik, define and test at least <strong>3 distinct properties</strong>.
 * You must use at least one custom {@code @Provide} method.
 *
 * <h3>Suggested properties (you may use these or design your own)</h3>
 * <ul>
 *   <li><b>Monotonicity</b> – For any fixed base and tax, increasing the discount
 *       rate never increases the final price.</li>
 *   <li><b>Identity</b> – A 0% discount and 0% tax returns exactly the base price.</li>
 *   <li><b>Boundedness</b> – The result is always &gt;= 0.</li>
 *   <li><b>Cart commutativity</b> – Adding product A then B yields the same total
 *       as adding B then A.</li>
 *   <li><b>Discount transitivity</b> – Applying a 10% then another 10% discount via
 *       {@code applyDiscount} is equivalent to a single call with the compounded rate
 *       (think carefully: is this actually true for this implementation?).</li>
 * </ul>
 *
 * <h3>For each property, include a comment that answers:</h3>
 * <ol>
 *   <li>What does this property mean in plain English?</li>
 *   <li>What class of bugs would this property catch?</li>
 * </ol>
 *
 * <h3>If jqwik finds a failing case</h3>
 * Do not just fix the test. Investigate the root cause and explain it in your
 * reflection report (include the counterexample jqwik printed).
 */
class ShopEasyPropertyTest {

    /**
     * Property: Monotonicity
     * 1. What does this property mean in plain English?
     *    If we keep the base price and tax same, raising the discount rate should never make the final price higher.
     * 2. What class of bugs would this property catch?
     *    Math calculation errors in the formula or wrong if-else structures that increase prices by mistake when discounts get bigger.
     */
    @Property
    void monotonicity(
            @ForAll @DoubleRange(min = 0.0, max = 5000.0) double base,
            @ForAll @DoubleRange(min = 0.0, max = 99.0) double discount1,
            @ForAll @DoubleRange(min = 0.0, max = 100.0) double tax
    ) {
        PriceCalculator calc = new PriceCalculator();
        double discount2 = discount1 + 1.0;

        double price1 = calc.calculate(base, discount1, tax);
        double price2 = calc.calculate(base, discount2, tax);

        assertThat(price2).isLessThanOrEqualTo(price1);
    }

    /**
     * Property: Identity
     * 1. What does this property mean in plain English?
     *    When there is no discount (0%) and no tax (0%), the calculated price must be exactly equal to the starting base price.
     * 2. What class of bugs would this property catch?
     *    Hardcoded values, bad initialization inside the constructor, or basic calculation errors when parameters are zero.
     */
    @Property
    void identity(@ForAll @DoubleRange(min = 0.0, max = 5000.0) double base) {
        PriceCalculator calc = new PriceCalculator();
        double result = calc.calculate(base, 0.0, 0.0);
        assertThat(result).isEqualTo(base);
    }

    /**
     * Property: Cart Commutativity
     * 1. What does this property mean in plain English?
     *    Adding a list of items to the cart in normal order or backwards order should give the exact same item count at the end.
     * 2. What class of bugs would this property catch?
     *    Order-dependent bugs, state synchronization issues inside the shopping cart, or structural problems when elements are appended.
     */
    @Property
    void cartCommutativity(@ForAll("validProductsAndQuantities") List<ProductQuantityPair> sequence) {
        ShoppingCart cart1 = new ShoppingCart();
        ShoppingCart cart2 = new ShoppingCart();

        for (ProductQuantityPair pair : sequence) {
            cart1.addItem(pair.product, pair.quantity);
        }

        for (int i = sequence.size() - 1; i >= 0; i--) {
            ProductQuantityPair pair = sequence.get(i);
            cart2.addItem(pair.product, pair.quantity);
        }

        assertThat(cart1.itemCount()).isEqualTo(cart2.itemCount());
    }

    @Provide
    Arbitrary<List<ProductQuantityPair>> validProductsAndQuantities() {
        Arbitrary<Product> products = Combinators.combine(
                Arbitraries.strings().alpha().ofMinLength(2).ofMaxLength(5),
                Arbitraries.doubles().between(0.5, 300.0)
        ).as((name, price) -> new Product("ID-" + Math.abs(name.hashCode()), name, price, 100));

        Arbitrary<Integer> quantities = Arbitraries.integers().between(1, 5);

        Arbitrary<ProductQuantityPair> pairs = Combinators.combine(products, quantities)
                .as(ProductQuantityPair::new);

        return pairs.list().ofMinSize(1).ofMaxSize(4);
    }

    static class ProductQuantityPair {
        final Product product;
        final int quantity;

        ProductQuantityPair(Product product, int quantity) {
            this.product = product;
            this.quantity = quantity;
        }
    }
}