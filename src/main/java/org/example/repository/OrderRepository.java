package org.example.repository;

import org.example.entity.Coffee;
import org.example.entity.Order;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderRepository {
    public static final OrderRepository Instance = new OrderRepository();
    private final DataSource dataSource = DataSourceProvider.Instance.getDataSource();

    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT id, user_id, total_price, created_at FROM orders")) {
            while (rs.next()) {
                orders.add(mapOrder(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get all orders", e);
        }
        for (Order order : orders) {
            order.setItems(getOrderItems(order.getId()));
        }
        return orders;
    }

    public void save(Order order) {
        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO orders(user_id, total_price, created_at) VALUES (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS)) {
                ps.setLong(1, order.getUserId());
                ps.setBigDecimal(2, BigDecimal.valueOf(order.getTotalPrice()));
                ps.setTimestamp(3, Timestamp.from(order.getCreatedAt()));
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        order.setId(keys.getLong(1));
                    }
                }
            }
            if (order.getItems() != null && !order.getItems().isEmpty()) {
                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO order_items(order_id, coffee_id) VALUES (?, ?)")) {
                    for (Coffee coffee : order.getItems()) {
                        ps.setLong(1, order.getId());
                        ps.setLong(2, coffee.getId());
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save order", e);
        }
    }

    public long getCount() {
        try (Connection conn = dataSource.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM orders")) {
            if (rs.next())
                return rs.getLong(1);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get order count", e);
        }
        return 0;
    }

    public Order getOrder(long id) {
        try (Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement(
                        "SELECT id, user_id, total_price, created_at FROM orders WHERE id = ?")) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Order order = mapOrder(rs);
                    order.setItems(getOrderItems(id));
                    return order;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get order", e);
        }
        return null;
    }

    public List<Order> getOrdersByCoffeeId(Long coffeeId) {
        String sql = "SELECT DISTINCT o.id, o.user_id, o.total_price, o.created_at " +
                "FROM orders o " +
                "JOIN order_items oi ON o.id = oi.order_id " +
                "WHERE oi.coffee_id = ?";
        List<Order> orders = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, coffeeId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    orders.add(mapOrder(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get orders by coffee id", e);
        }
        for (Order order : orders) {
            order.setItems(getOrderItems(order.getId()));
        }
        return orders;
    }

    private List<Coffee> getOrderItems(long orderId) {
        String sql = "SELECT c.id, c.name, c.price FROM coffee c " +
                "JOIN order_items oi ON c.id = oi.coffee_id " +
                "WHERE oi.order_id = ?";
        List<Coffee> items = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    items.add(new Coffee(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get order items", e);
        }
        return items;
    }

    private Order mapOrder(ResultSet rs) throws SQLException {
        return new Order(rs);
    }
}
