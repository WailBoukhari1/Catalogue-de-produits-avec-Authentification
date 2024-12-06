package com.youcode.product_manage.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@Profile("dev")
public class DevSecurityConfig {
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(csrf -> csrf.disable())
            .headers(headers -> headers
                .frameOptions(frame -> frame.disable()))
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.ALWAYS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/h2-console/**").permitAll()
                .anyRequest().permitAll())
            .build();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return authentication -> {
            throw new BadCredentialsException("Development mode - authentication disabled");
        };
    }
//
//    @Bean
//    public UserDetailsService userDetailsService() {
//        UserDetails user = User.builder()
//            .username("user")
//            .password("{noop}password")
//            .roles("USER")
//            .build();
//
//        UserDetails admin = User.builder()
//            .username("admin")
//            .password("{noop}password")
//            .roles("ADMIN")
//            .build();
//
//        return new InMemoryUserDetailsManager(user, admin);
//    }
}
