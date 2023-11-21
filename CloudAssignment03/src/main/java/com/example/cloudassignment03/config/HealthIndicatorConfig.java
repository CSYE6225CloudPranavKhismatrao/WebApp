package com.example.cloudassignment03.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.jdbc.DataSourceHealthIndicator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class HealthIndicatorConfig {

    private final DataSource dataSource;

    public HealthIndicatorConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Bean
    public HealthIndicator smartDBHealthIndicator() {
        return new DataSourceHealthIndicator(dataSource , "SELECT 1");
    }
}
