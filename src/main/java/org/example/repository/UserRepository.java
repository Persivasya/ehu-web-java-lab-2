package org.example.repository;

import org.example.entity.User;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserRepository {
    public static final UserRepository Instance = new UserRepository();
    private final DataSource dataSource = DataSourceProvider.Instance.getDataSource();

    public int getTotalUsers() {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users")) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get total users", e);
        }
        return 0;
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, name FROM users")) {
            while (rs.next()) {
                users.add(mapUser(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get all users", e);
        }
        return users;
    }

    public User getUser(long id) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT id, name FROM users WHERE id = ?")) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapUser(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get user by id", e);
        }
        return null;
    }

    public List<User> getUsersByOrderedCoffee(Long coffeeId) {
        String sql = "SELECT DISTINCT u.id, u.name FROM users u " +
                "JOIN orders o ON u.id = o.user_id " +
                "JOIN order_items oi ON o.id = oi.order_id " +
                "WHERE oi.coffee_id = ?";
        List<User> users = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, coffeeId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) users.add(mapUser(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get users by ordered coffee", e);
        }
        return users;
    }

    public User getUserByUserName(String name) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT id, name FROM users WHERE name = ?")) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapUser(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get user by username", e);
        }
        return null;
    }

    public User save(User user) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO users(name) VALUES (?) RETURNING id")) {
            ps.setString(1, user.getName());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    user.setId(rs.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save user", e);
        }
        return user;
    }

    private User mapUser(ResultSet rs) throws SQLException {
        return new User(rs);
    }
}
