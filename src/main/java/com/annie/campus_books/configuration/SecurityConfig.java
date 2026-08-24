package com.annie.campus_books.configuration;

import com.annie.campus_books.security.filter.JwtAuthenticationFilter;
import com.annie.campus_books.service.JwtService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtService jwtService(@Value("${jwt.secret-key}") String secretKeyStr,
                                 @Value("${jwt.valid-seconds}") int validSeconds) {
        return new JwtService(secretKeyStr, validSeconds);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity, JwtAuthenticationFilter jwtAuthenticationFilter) {
        httpSecurity.
                formLogin(AbstractHttpConfigurer::disable).
                csrf(AbstractHttpConfigurer::disable).
                authorizeHttpRequests(requests -> requests.
                        //permitAll
                                requestMatchers(HttpMethod.GET, "/api.campus-books.com/books/**").permitAll().
                        requestMatchers(HttpMethod.POST, "/api.campus-books.com/books/users/*?").permitAll().
                        // hasAuthority
                                requestMatchers(HttpMethod.GET, "/api.campus-books.com/users/").hasAuthority("ADMIN").
                        requestMatchers(HttpMethod.GET, "/api.campus-books.com/users/*?/**").hasAuthority("ADMIN").
                        requestMatchers(HttpMethod.PATCH, "/api.campus-books.com/users/*?").hasAuthority("ADMIN").
                        requestMatchers(HttpMethod.PATCH, "/api.campus-books.com/users/current-user/password").hasAuthority("USER").
                        requestMatchers(HttpMethod.DELETE, "/api.campus-books.com/users/*?").hasAuthority("ADMIN").
                        requestMatchers(HttpMethod.GET, "/api.campus-books.com/orders/").hasAuthority("ADMIN").
                        // authenticated
                                anyRequest().authenticated()).
                addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }

}
