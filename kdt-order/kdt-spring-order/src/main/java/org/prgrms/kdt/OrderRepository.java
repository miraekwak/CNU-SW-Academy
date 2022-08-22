package org.prgrms.kdt;

import org.prgrms.kdt.order.Order;

public interface OrderRepository {
    Order insert(Order order);
}
