package com.example.cloudassignment03.auth;

import com.example.cloudassignment03.entity.User;
import com.example.cloudassignment03.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class BasicAuthenticationManager implements AuthenticationManager {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getPrincipal() + "";
        String password = authentication.getCredentials() + "";
        System.out.println("In AuthenticateManager " );
        System.out.println("Username " + username + " Pass " + password);
        Optional<User> user =  userRepository.findByEmail(username);
        if (user == null) {
            throw new BadCredentialsException("1000");
        }
        if (!passwordEncoder.matches(password, user.get().getPassword())) {
            throw new BadCredentialsException("1000");
        }
//        if (user.get().isDisabled()) {
//            throw new DisabledException("1001");
//        }
//        List<Right> userRights = rightRepo.getUserRights(username);
//        List<SimpleGrantedAuthority> grantedAuthorities = new ArrayList<>();
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_USER");
        List<GrantedAuthority> list = new ArrayList<>();
        list.add(authority);
        return new UsernamePasswordAuthenticationToken(username,password,list);
//        new SimpleGrantedAuthority(user.get().getEmail())), username, "");
//                userRights.stream().map(x -> new SimpleGrantedAuthority(x.getName())).collect(Collectors.toList()));
    }


}
