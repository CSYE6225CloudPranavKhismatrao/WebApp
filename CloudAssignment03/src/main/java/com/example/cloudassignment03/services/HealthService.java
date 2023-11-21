package com.example.cloudassignment03.services;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;

import org.springframework.stereotype.Service;

import javax.sql.DataSource;

@Service
public class HealthService {

    @PersistenceContext
    private EntityManager entityManager;

    Logger logger = LoggerFactory.getLogger("jsonLogger");

    public Health checkDataSourceHealth() {
        Query query = entityManager.createQuery("select 1 from Account");
        if(!query.getResultList().isEmpty()) {
            logger.atInfo().log("Data Source is UP");
            return Health.status(Status.UP).build();
        }
        logger.atError().log("Data Source is DOWN");
        return Health.status(Status.DOWN).build();

    }



}



