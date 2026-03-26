package org.example.repository;

import org.example.entity.Order;

import java.util.List;

public class OrderRepository {
    public static final OrderRepository Instance = new OrderRepository();

    public List<OrderRepository> getAllOrders() {
        return null;
    }

    public void save(Order order) {

    }

    public long getCount() {
        return 0;
    }

    public Order getOrder(long id) {
        return null;
    }

    public List<Order> getOrdersByCoffeeId(Long id) {
        return null;
    }
}
