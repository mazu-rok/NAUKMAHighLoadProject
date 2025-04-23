package ua.edu.ukma.balancer.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
public class GatewayConfig {
    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection") ServerHttpSecurity security) {
        security.csrf(ServerHttpSecurity.CsrfSpec::disable);

        return security.build();
    }
}
