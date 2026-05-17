package shopeasy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ShoppingCartStructuralTest {

    private ShoppingCart cart;
    private Product apple;
    private Product banana;

    @BeforeEach
    void setUp() {
        cart = new ShoppingCart();
        apple = new Product("P001", "Apple", 1.50, 100);
        banana = new Product("P002", "Banana", 0.80, 50);
    }

    @Test
    void addItem_newProduct_shouldAdd() {
        cart.addItem(apple, 2);
        assertThat(cart.itemCount()).isEqualTo(1);
    }

    @Test
    void addItem_existingProduct_shouldIncreaseQuantity() {
        cart.addItem(apple, 2);
        cart.addItem(apple, 3);

        assertThat(cart.getItems().get(0).getQuantity()).isEqualTo(5);
    }

    @Test
    void addItem_multipleProducts_shouldIncreaseDistinctCount() {
        cart.addItem(apple, 1);
        cart.addItem(banana, 1);

        assertThat(cart.itemCount()).isEqualTo(2);
    }

    @Test
    void addItem_nullProduct_shouldThrowException() {
        assertThatThrownBy(() -> cart.addItem(null, 2))
                .isInstanceOf(Throwable.class);
    }

    @Test
    void addItem_invalidQuantity_shouldThrowException() {
        assertThatThrownBy(() -> cart.addItem(apple, 0))
                .isInstanceOf(Throwable.class);
    }

    @Test
    void addItem_negativeQuantity_shouldThrowException() {
        assertThatThrownBy(() -> cart.addItem(apple, -5))
                .isInstanceOf(Throwable.class);
    }

    @Test
    void removeItem_existing_shouldRemove() {
        cart.addItem(apple, 2);
        cart.removeItem("P001");

        assertThat(cart.itemCount()).isZero();
    }

    @Test
    void removeItem_notExisting_shouldDoNothing() {
        cart.addItem(apple, 2);
        cart.removeItem("XXX");

        assertThat(cart.itemCount()).isEqualTo(1);
    }

    @Test
    void removeItem_emptyCart_shouldNotFail() {
        cart.removeItem("P001");
        assertThat(cart.itemCount()).isZero();
    }

    @Test
    void removeItem_nullId_shouldNotFailOrThrow() {
        cart.addItem(apple, 2);
        cart.removeItem(null);
        assertThat(cart.itemCount()).isEqualTo(1);
    }

    @Test
    void updateQuantity_valid_shouldUpdate() {
        cart.addItem(apple, 2);
        cart.updateQuantity("P001", 10);

        assertThat(cart.getItems().get(0).getQuantity()).isEqualTo(10);
    }

    @Test
    void updateQuantity_notFound_shouldThrow() {
        assertThatThrownBy(() -> cart.updateQuantity("X", 5))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void updateQuantity_invalidQuantity_shouldThrow() {
        cart.addItem(apple, 2);

        assertThatThrownBy(() -> cart.updateQuantity("P001", 0))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void updateQuantity_negativeQuantity_shouldThrow() {
        cart.addItem(apple, 2);

        assertThatThrownBy(() -> cart.updateQuantity("P001", -5))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void updateQuantity_nullId_shouldThrow() {
        assertThatThrownBy(() -> cart.updateQuantity(null, 5))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void applyDiscount_zero_shouldReturnSame() {
        cart.addItem(apple, 2);

        double result = cart.applyDiscount(0);

        assertThat(result).isEqualTo(cart.total());
    }

    @Test
    void applyDiscount_full_shouldReturnZero() {
        cart.addItem(apple, 2);

        assertThat(cart.applyDiscount(100)).isEqualTo(0.0);
    }

    @Test
    void applyDiscount_partial_shouldReduce() {
        cart.addItem(apple, 10);

        double before = cart.total();
        double after = cart.applyDiscount(50);

        assertThat(after).isLessThan(before);
    }

    @Test
    void applyDiscount_invalidNegative_shouldThrow() {
        assertThatThrownBy(() -> cart.applyDiscount(-5))
                .isInstanceOf(Throwable.class);
    }

    @Test
    void applyDiscount_invalidOverHundred_shouldThrow() {
        assertThatThrownBy(() -> cart.applyDiscount(120))
                .isInstanceOf(Throwable.class);
    }

    @Test
    void total_empty_shouldBeZero() {
        assertThat(cart.total()).isEqualTo(0.0);
    }

    @Test
    void total_multipleItems_shouldSumCorrectly() {
        cart.addItem(apple, 2);
        cart.addItem(banana, 5);

        assertThat(cart.total()).isEqualTo(7.0);
    }

    @Test
    void clear_shouldEmptyCart() {
        cart.addItem(apple, 2);
        cart.addItem(banana, 3);

        cart.clear();

        assertThat(cart.itemCount()).isZero();
    }

    @Test
    void getItems_shouldBeImmutable() {
        assertThatThrownBy(() -> cart.getItems().add(null))
                .isInstanceOf(UnsupportedOperationException.class);
    }
}