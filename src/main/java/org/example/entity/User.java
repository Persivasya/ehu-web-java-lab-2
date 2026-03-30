package org.example.entity;

import lombok.Data;

import java.sql.ResultSet;
import java.sql.SQLException;

@Data
public class User {
    private Long id;
    private String name;

    public User() {
    }

    public User(ResultSet rs) throws SQLException {
        this.id = rs.getLong("id");
        this.name = rs.getString("name");
    }
}
