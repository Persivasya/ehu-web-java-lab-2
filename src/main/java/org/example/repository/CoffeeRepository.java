package org.example.repository;

import org.example.dto.CoffeeStats;
import org.example.entity.Coffee;

import javax.sql.DataSource;
import java.util.List;

public class CoffeeRepository {
    public static final CoffeeRepository Instance = new CoffeeRepository();
    private DataSource dataSource = DataSourceProvider.Instance.getDataSource();

    public List<Coffee> getAllCoffees() {
        return null;
    }

    public Coffee getCoffeById(long id) {
        return null;
    }

    public CoffeeStats getMosPopularCoffee() {
        return null;
    }

    public CoffeeStats getLeastPopularCoffee() {
        return null;
    }

    //Optionally implement this method to return the coffee that generated the most revenue
    public CoffeeStats getMostRevenueGeneratingCoffee() {
        return null;
    }
}
