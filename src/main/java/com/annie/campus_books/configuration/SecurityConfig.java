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
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtService jwtService(@Value("${jwt.secret-key}") String secretKeyStr,
                                 @Value("${jwt.access-valid-millis}") long accessValidMillis,
                                 @Value("${jwt.refresh-valid-millis}") long refreshValidMillis) {
        return new JwtService(secretKeyStr, accessValidMillis, refreshValidMillis);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity, JwtAuthenticationFilter jwtAuthenticationFilter) {
        PathPatternRequestMatcher.Builder builder = PathPatternRequestMatcher.withDefaults().basePath("/api.campus-books.com");
        httpSecurity.
                formLogin(AbstractHttpConfigurer::disable).
                csrf(AbstractHttpConfigurer::disable).
                authorizeHttpRequests(requests -> requests.
                        //permitAll
                                requestMatchers(builder.matcher(HttpMethod.GET, "/books/**")).permitAll().
                        requestMatchers(builder.matcher(HttpMethod.POST, "/users/register")).permitAll().
                        requestMatchers(builder.matcher(HttpMethod.POST, "/users/login")).permitAll().
                        requestMatchers(builder.matcher(HttpMethod.POST, "/users/refresh")).permitAll().
                        // hasAuthority
                                requestMatchers(builder.matcher(HttpMethod.GET, "/users/")).hasAuthority("ADMIN").
                        requestMatchers(builder.matcher(HttpMethod.PATCH, "/users/current/password")).hasAuthority("USER").
                        requestMatchers(builder.matcher(HttpMethod.GET, "/orders/*")).hasAuthority("ADMIN").
                        // authenticated
                                anyRequest().authenticated()).
                sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)).
                addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }

}
