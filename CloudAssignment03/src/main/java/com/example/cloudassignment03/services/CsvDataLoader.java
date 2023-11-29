package com.example.cloudassignment03.services;

import com.example.cloudassignment03.entity.Account;
import com.example.cloudassignment03.repository.AccountRepository;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
public class CsvDataLoader {
    private final AccountRepository accountRepository;

    public CsvDataLoader(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Value("${env.CSV_PATH:./opt/users.csv}")
    private String csv_path;

    @PostConstruct
    public void loadDataFromCsv() {
        log.atInfo().log("Current Working Directory: " + System.getProperty("user.dir"));
        log.atWarn().log("CSV Path: " + csv_path);
        try (CSVReader csvReader = new CSVReader(new FileReader(csv_path))) {
            String[] line;
            csvReader.readNext();
            while ((line = csvReader.readNext()) != null) {
                String username = line[0]; // Assuming the username is in the first column
                String lastName = line[1];
                String email = line[2]; // Assuming the password is in the second column
                String password = line[3];
                log.info("Extracted new Account details");

                // Check if the user already exists
                Optional<Account> existingUser = accountRepository.findByEmail(email);

                if (existingUser.isEmpty()) {
                    Account newUser = new Account();
                    newUser.setFirstName(username);
                    newUser.setLastName(lastName);
                    newUser.setEmail(email);
                    newUser.setAccountCreated(LocalDateTime.now());
                    newUser.setAccountUpdated(LocalDateTime.now());
                    log.info("Created new account with timestamp");
                    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
                    newUser.setPassword(passwordEncoder.encode(password));
                    log.info("Saved Account to database");
                    accountRepository.save(newUser);
                }
            }
        } catch (IOException e) {
            log.error(e.getMessage());
//            e.printStackTrace();
        } catch (CsvValidationException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}

