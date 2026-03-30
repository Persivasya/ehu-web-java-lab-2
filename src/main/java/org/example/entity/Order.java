package org.example.entity;

import lombok.Data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.List;

@Data
public class Order {
    private Long id;
    private Long userId;
    private List<Coffee> items;
    private Instant createdAt;
    private double totalPrice;

    public Order() {
    }

    public Order(ResultSet rs) throws SQLException {
        this.id = rs.getLong("id");
        this.userId = rs.getLong("user_id");
        this.totalPrice = rs.getDouble("total_price");
        this.createdAt = rs.getTimestamp("created_at").toInstant();
    }
}
