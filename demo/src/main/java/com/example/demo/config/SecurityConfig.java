package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // desativa CSRF (para testes)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/cadastro", "/usuarios", "/css/**", "/js/**").permitAll()
                .anyRequest().permitAll()
            )
            .formLogin(form -> form.disable()); // remove o login padrão
        return http.build();
    }
}
