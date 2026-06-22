package com.foodmarket.gateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.List;

@Component
@Slf4j
public class JwtAuthFilter implements GlobalFilter, Ordered {

    @Value("${jwt.secret}")
    private String secret;

    private static final List<String> PUBLIC_ROUTES = List.of(
            "/auth/register", "/auth/login",
            "/search/restaurants"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        log.info("[GATEWAY] Ruta permitida (sin autenticacion): {}", path);

        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                Key key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(key).build()
                        .parseClaimsJws(token).getBody();
                String email = claims.getSubject();
                String role  = (String) claims.get("role");
                log.info("[GATEWAY] Token presente: usuario={} rol={}", email, role);
                ServerHttpRequest mutated = exchange.getRequest().mutate()
                        .header("X-User-Email", email)
                        .header("X-User-Role",  role)
                        .build();
                return chain.filter(exchange.mutate().request(mutated).build());
            } catch (JwtException e) {
                log.warn("[GATEWAY] Token invalido ignorado: {}", e.getMessage());
            }
        }

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() { return -1; }
}
