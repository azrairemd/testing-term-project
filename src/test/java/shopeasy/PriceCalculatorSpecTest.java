package shopeasy;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Task 1 – Specification-Based Testing (Chapter 2)
 *
 * <p>Target class: {@link PriceCalculator}
 *
 * <p>Your goal is to test {@code PriceCalculator.calculate(basePrice, discountRate, taxRate)}
 * using the domain testing technique from Chapter 2:
 * <ol>
 *   <li>Identify equivalence partitions for each input dimension.</li>
 *   <li>Identify boundary values between partitions (on-point / off-point).</li>
 *   <li>Write at least 10 meaningful test cases that cover both partitions and boundaries.</li>
 *   <li>Use {@code @ParameterizedTest} with {@code @CsvSource} for tests that share structure.</li>
 *   <li>Add a comment above each test method explaining which partition or boundary it covers.</li>
 * </ol>
 *
 * <h3>Input dimensions to consider</h3>
 * <ul>
 *   <li><b>basePrice</b>  – zero, positive, very large</li>
 *   <li><b>discountRate</b> – 0 (no discount), (0,100) typical, 100 (full discount)</li>
 *   <li><b>taxRate</b>    – 0 (no tax), (0,100) typical, 100 (100% tax)</li>
 * </ul>
 */
class PriceCalculatorSpecTest {

    private PriceCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new PriceCalculator();
    }

    // -----------------------------------------------------------------------
    // TODO: Write your tests below.
    //
    // EXAMPLE STRUCTURE (replace with real cases):
    //
    // /** Partition: zero base price — result must always be 0 regardless of rates */
    // @Test
    // void zeroPriceAlwaysReturnsZero() {
    //     assertThat(calculator.calculate(0, 20, 10)).isEqualTo(0.0);
    // }
    //
    // /** Boundary: discountRate at lower bound (0%) — no reduction applied */
    // @Test
    // void discountRateZeroMeansNoDiscount() {
    //     double result = calculator.calculate(100, 0, 0);
    //     assertThat(result).isEqualTo(100.0);
    // }
    //
    // /** Boundary: discountRate at upper bound (100%) — full discount wipes price to 0 */
    // @Test
    // void discountRateHundredMeansFullDiscount() {
    //     double result = calculator.calculate(100, 100, 0);
    //     assertThat(result).isEqualTo(0.0);
    // }
    //
    // /** Partition: typical values — check formula correctness */
    // @ParameterizedTest(name = "base={0}, disc={1}%, tax={2}% => {3}")
    // @CsvSource({
    //     "100.0, 10.0, 20.0, 108.0",
    //     "200.0,  0.0, 10.0, 220.0",
    // })
    // void typicalValues(double base, double disc, double tax, double expected) {
    //     assertThat(calculator.calculate(base, disc, tax)).isCloseTo(expected, within(0.001));
    // }
    // -----------------------------------------------------------------------

    // Partition: zero base price — result should always be 0 
    @Test
    void zeroBasePriceShouldReturnZero() {
        double result = calculator.calculate(0, 20, 10);
        assertThat(result).isEqualTo(0.0);
    }

    // Boundary: discountRate = 0 means no discount applied 
    @Test
    void zeroDiscountShouldNotChangePrice() {
        double result = calculator.calculate(100, 0, 0);
        assertThat(result).isEqualTo(100.0);
    }

    // Boundary: discountRate = 100 means full discount (price becomes 0) 
    @Test
    void fullDiscountShouldReturnZero() {
        double result = calculator.calculate(100, 100, 0);
        assertThat(result).isEqualTo(0.0);
    }

    // Boundary: taxRate = 0 means no tax applied 
    @Test
    void zeroTaxShouldNotChangePrice() {
        double result = calculator.calculate(100, 10, 0);
        assertThat(result).isEqualTo(90.0);
    }

    // Boundary: taxRate = 100 doubles the discounted price 
    @Test
    void fullTaxShouldDoubleDiscountedPrice() {
        double result = calculator.calculate(100, 50, 100);
        assertThat(result).isEqualTo(100.0);
    }

    // Partition: typical values — normal calculation check 
    @Test
    void typicalCase_shouldWorkCorrectly() {
        double result = calculator.calculate(200, 10, 20);
        assertThat(result).isEqualTo(216.0);
    }

    // Boundary: taxRate = 100% should fully apply tax after discount 
    @Test
    void fullTaxBoundary_shouldApplyCorrectly() {
        double result = calculator.calculate(100, 20, 100);
        // 100 -> %20 discount = 80
        // 80 -> %100 tax = 160
        assertThat(result).isEqualTo(160.0);
    }

    // Boundary: small discount with tax applied 
    @Test
    void smallDiscount_shouldWorkWithTax() {
        double result = calculator.calculate(100, 1, 10);
        assertThat(result).isEqualTo(108.9);
    }

    // Partition: very large base price 
    @Test
    void veryLargeBasePrice_shouldBeHandledCorrectly() {
        double result = calculator.calculate(1_000_000, 10, 20);
        assertThat(result).isEqualTo(1_080_000.0);
    }

    // Partition: no discount, only tax applied 
    @Test
    void noDiscount_onlyTaxApplied() {
        double result = calculator.calculate(200, 0, 50);
        assertThat(result).isEqualTo(300.0);
    }
}
