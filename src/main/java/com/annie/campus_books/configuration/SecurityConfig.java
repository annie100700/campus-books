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
                        requestMatchers(HttpMethod.GET,"/campus-books/books").permitAll().
                        requestMatchers(HttpMethod.GET,"/campus-books/book/*").permitAll().
                        requestMatchers(HttpMethod.POST,"/campus-books/user/register").permitAll().
                        requestMatchers(HttpMethod.POST,"/campus-books/user/login").permitAll().
                        requestMatchers(HttpMethod.PUT,"/campus-books/user/*").hasAnyAuthority("USER","ADMIN").
                        requestMatchers(HttpMethod.DELETE,"/campus-books/user/*").hasAnyAuthority("USER","ADMIN").
                        requestMatchers(HttpMethod.GET,"/campus-books/users").hasAuthority("ADMIN").
                        requestMatchers(HttpMethod.GET,"/campus-books/user/*").hasAuthority("ADMIN").
                        requestMatchers(HttpMethod.DELETE,"/campus-books/order/{orderId}").hasAuthority("ADMIN").
                        anyRequest().authenticated());
        return http.build();
    }

}
