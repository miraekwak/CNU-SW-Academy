package org.prgrms.kdt.order;

import java.util.UUID;

public class OrderItem {
    public final UUID productId;
    public final long productPrice;
    public final Long quantity;

    public OrderItem(UUID productId, long productPrice, Long quantity) {
        this.productId = productId;
        this.productPrice = productPrice;
        this.quantity = quantity;
    }

    public UUID getProductId() {
        return productId;
    }

    public long getProductPrice() {
        return productPrice;
    }

    public Long getQuantity() {
        return quantity;
    }
}
