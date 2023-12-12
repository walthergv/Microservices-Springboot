package com.walther.api_gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
                .csrf(csrfSpec -> csrfSpec.disable())
                .authorizeExchange(
                        authorizeExchangeSpec -> authorizeExchangeSpec
                                .anyExchange().authenticated())
                .oauth2Login(oAuth2LoginSpec -> {});
        return http.build();
    }
}
