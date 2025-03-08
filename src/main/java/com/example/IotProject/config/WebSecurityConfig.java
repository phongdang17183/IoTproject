package com.example.IotProject.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.IotProject.component.CustomAccessDeniedHandler;
import com.example.IotProject.component.CustomAuthenticationEntryPoint;
import com.example.IotProject.filter.JwtTokenFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {
    private final JwtTokenFilter jwtTokenFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Tắt tính năng CSRF (Cross-Site Request Forgery) trong Spring Security.
        http
                .csrf(AbstractHttpConfigurer::disable)
                // add a filter before the UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                // config rules for authorize requests
                .authorizeHttpRequests(requests -> {
                    requests.requestMatchers("/api/v1/admin/**").hasAuthority("ROLE_USER")
                            .requestMatchers("/api/v1/**").permitAll()
                            .requestMatchers("/api/v1/chat/**").permitAll()
                            .requestMatchers("/ws").permitAll()
                            .requestMatchers(HttpMethod.GET, "/images/**").permitAll()
                            .anyRequest().authenticated();

                });
        return http.build();
    }
}