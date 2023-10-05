package com.example.cloudassignment03.repository;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.SQLException;

@Component
public class MyHealthIndicator implements HealthIndicator {
    private final DataSource dataSource;

    public MyHealthIndicator(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private boolean checkServiceStatus() {
        try {
            return dataSource.getConnection() != null;
        } catch (SQLException e) {
            return false;
        }

    }

    @Override
    public Health health() {
        boolean isServiceHealthy = checkServiceStatus(); // Replace with your logic
        if (isServiceHealthy) {
            return Health.up().build();
        } else {
            return Health.down().build();
        }
    }
}
