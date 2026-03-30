package org.example.entity;

import lombok.Data;

import java.sql.ResultSet;
import java.sql.SQLException;

@Data
public class Coffee {
    private Long id;
    private String name;
    private double price;

    public Coffee() {
    }

    public Coffee(String name, double price, int quantity) {
        this.name = name;
        this.price = price;
    }

    public Coffee(ResultSet rs) throws SQLException {
        this.id = rs.getLong("id");
        this.name = rs.getString("name");
        this.price = rs.getDouble("price");
    }
}
