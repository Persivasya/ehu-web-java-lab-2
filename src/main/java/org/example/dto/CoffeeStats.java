package org.example.dto;

import lombok.Data;
import org.example.entity.Coffee;

@Data
public class CoffeeStats {

    private Coffee coffee;
    private long totalOrders;
    private double revenue;
}
