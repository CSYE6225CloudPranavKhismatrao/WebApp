package com.example.cloudassignment03.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.jdbc.DataSourceHealthIndicator;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.SQLException;

//@Component
//public class MyHealthIndicator extends DataSourceHealthIndicator {
//
//
//    @Override
//    protected void doHealthCheck(Health.Builder builder) throws Exception {
//        super.doHealthCheck(builder);
//        builder.up().withDetail("message", "Database is up");
//    }
//
//
//}
