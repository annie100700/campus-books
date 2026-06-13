package com.annie.campus_books.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        http.
                formLogin(AbstractHttpConfigurer::disable).
                csrf(AbstractHttpConfigurer::disable).
                httpBasic(Customizer.withDefaults()).
                authorizeHttpRequests(requests -> requests.
                        requestMatchers(HttpMethod.GET,"/api.campus-books.com/books/").permitAll().
                        requestMatchers(HttpMethod.GET,"/api.campus-books.com/books/*").permitAll().
                        requestMatchers(HttpMethod.POST,"/api.campus-books.com/users/register").permitAll().
                        requestMatchers(HttpMethod.POST,"/api.campus-books.com/users/login").permitAll().
                        requestMatchers(HttpMethod.GET,"/api.campus-books.com/books/search").permitAll().
                        requestMatchers(HttpMethod.PUT,"/api.campus-books.com/users/*").hasAnyAuthority("USER","ADMIN").
                        requestMatchers(HttpMethod.DELETE,"/api.campus-books.com/users/*").hasAnyAuthority("USER","ADMIN").
                        requestMatchers(HttpMethod.GET,"/api.campus-books.com/users/").hasAuthority("ADMIN").
                        requestMatchers(HttpMethod.GET,"/api.campus-books.com/users/*").hasAuthority("ADMIN").
                        requestMatchers(HttpMethod.DELETE,"/api.campus-books.com/orders/*").hasAuthority("ADMIN").
                        anyRequest().authenticated());
        return http.build();
    }

}
