package com.intellidesk.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // Simply pass through all requests - let backend services handle authentication
        // This avoids double authentication and allows backend services to control their own security
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -1; // Execute before routing
    }
}
