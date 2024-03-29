package com.example.cloudassignment03.auth;

import com.example.cloudassignment03.entity.Account;
import com.example.cloudassignment03.repository.AccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class BasicAuthenticationManager implements AuthenticationManager {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    Logger logger = LoggerFactory.getLogger("jsonLogger");


    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getPrincipal() + "";
        String password = authentication.getCredentials() + "";

        Optional<Account> user =  accountRepository.findByEmail(username);
        if (user.isEmpty()) {
            logger.atError().log("User authentication failed");
            logger.atDebug().log("User Not Found");
            throw new BadCredentialsException("1000");
        }
        if (!passwordEncoder.matches(password, user.get().getPassword())) {
            logger.atError().log("User authentication failed");
            logger.atDebug().log("Credentials Invalid");

            throw new BadCredentialsException("1000");
        }

        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_USER");
        List<GrantedAuthority> list = new ArrayList<>();
        list.add(authority);
        logger.atInfo().log("User authenticated Successfully");

        return new UsernamePasswordAuthenticationToken(username,password,list);
    }


}
