package com.example.cloudassignment03.services;

//import com.example.cloudassignment02.component.MyHealthIndicator;
import com.example.cloudassignment03.repository.MyHealthIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.stereotype.Service;

@Service
public class HealthService {

    @Autowired
    private MyHealthIndicator myHealthIndicator;

    public boolean checkDatabaseConnection(){
         Health health = myHealthIndicator.getHealth(false);
         Status res =  health.getStatus();
         System.out.println(res);
         return res.equals(Status.UP);

    }

}
