package org.example.repository;

import org.example.dto.CoffeeStats;
import org.example.entity.Coffee;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CoffeeRepository {
    public static final CoffeeRepository Instance = new CoffeeRepository();
    private final DataSource dataSource = DataSourceProvider.Instance.getDataSource();
    private List<Coffee> cache = null;

    public List<Coffee> getAllCoffees() {
        if (cache != null) return cache;
        List<Coffee> coffees = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, name, price FROM coffee")) {
            while (rs.next()) {
                coffees.add(mapCoffee(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get all coffees", e);
        }
        cache = coffees;
        return coffees;
    }

    public Coffee getCoffeById(long id) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT id, name, price FROM coffee WHERE id = ?")) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapCoffee(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get coffee by id", e);
        }
        return null;
    }

    public CoffeeStats getMosPopularCoffee() {
        String sql = "SELECT c.id, c.name, c.price, COUNT(oi.coffee_id) AS total_orders " +
                "FROM coffee c " +
                "JOIN order_items oi ON c.id = oi.coffee_id " +
                "GROUP BY c.id, c.name, c.price " +
                "ORDER BY total_orders DESC " +
                "LIMIT 1";
        return queryPopularityStats(sql);
    }

    public CoffeeStats getLeastPopularCoffee() {
        String sql = "SELECT c.id, c.name, c.price, COUNT(oi.coffee_id) AS total_orders " +
                "FROM coffee c " +
                "JOIN order_items oi ON c.id = oi.coffee_id " +
                "GROUP BY c.id, c.name, c.price " +
                "ORDER BY total_orders ASC " +
                "LIMIT 1";
        return queryPopularityStats(sql);
    }

    public CoffeeStats getMostRevenueGeneratingCoffee() {
        String sql = "SELECT c.id, c.name, c.price, SUM(c.price) AS revenue " +
                "FROM coffee c " +
                "JOIN order_items oi ON c.id = oi.coffee_id " +
                "GROUP BY c.id, c.name, c.price " +
                "ORDER BY revenue DESC " +
                "LIMIT 1";
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                CoffeeStats stats = new CoffeeStats();
                stats.setCoffee(mapCoffee(rs));
                stats.setRevenue(rs.getDouble("revenue"));
                return stats;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get most revenue generating coffee", e);
        }
        return null;
    }

    private CoffeeStats queryPopularityStats(String sql) {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                CoffeeStats stats = new CoffeeStats();
                stats.setCoffee(mapCoffee(rs));
                stats.setTotalOrders(rs.getLong("total_orders"));
                return stats;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to query coffee popularity stats", e);
        }
        return null;
    }

    private Coffee mapCoffee(ResultSet rs) throws SQLException {
        return new Coffee(rs);
    }
}
