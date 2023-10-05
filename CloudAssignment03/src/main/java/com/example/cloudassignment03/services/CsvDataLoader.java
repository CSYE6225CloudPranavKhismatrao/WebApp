package com.example.cloudassignment03.services;

import com.example.cloudassignment03.entity.User;
import com.example.cloudassignment03.repository.UserRepository;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class CsvDataLoader {
    private final UserRepository userRepository;

    public CsvDataLoader(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostConstruct
    public void loadDataFromCsv() {
        System.out.println("Current Working Directory: " + System.getProperty("user.dir"));

        try (CSVReader csvReader = new CSVReader(new FileReader("..//opt/users.csv"))) {
            String[] line;
            csvReader.readNext();
            while ((line = csvReader.readNext()) != null) {
                String username = line[0]; // Assuming the username is in the first column
                String lastName = line[1];
                String email = line[2]; // Assuming the password is in the second column
                String password = line[3];

                // Check if the user already exists
                Optional<User> existingUser = userRepository.findByEmail(email);

                if (existingUser.isEmpty()) {
                    User newUser = new User();
                    newUser.setFirstName(username);
                    newUser.setLastName(lastName);
                    newUser.setEmail(email);
                    newUser.setAccountCreated(LocalDateTime.now());
                    newUser.setAccountUpdated(LocalDateTime.now());
                    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
                    newUser.setPassword(passwordEncoder.encode(password));
//                    newUser.setId(1L);
//                    newUser.setPassword(password);
                    // Set other fields if needed
                    userRepository.save(newUser);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CsvValidationException e) {
            throw new RuntimeException(e);
        }
    }
}

