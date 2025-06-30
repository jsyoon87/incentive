package com.mintit.incentive.configure.security;

import java.util.Objects;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class AuthConverter implements ServerAuthenticationConverter {

    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {

        if (exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION) == null) {
            return Mono.empty();
        }

        return Mono.justOrEmpty(exchange.getRequest())
                   .filter(request -> {
                       String requestPath = request.getPath().toString();
                       return !requestPath.startsWith("/auth") && !(Objects.equals(request.getMethod(), HttpMethod.POST) && requestPath.startsWith("/users")) && !(Objects.equals(request.getMethod(), HttpMethod.GET) && requestPath.startsWith("/terms")) && !(Objects.equals(request.getMethod(), HttpMethod.POST) && requestPath.startsWith("/terms/choice"));

                   })
                   .map(v -> {
                       String token = v.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
                       if (token != null) {
                           if (!token.startsWith("Bearer")) {
                               token = "Bearer " + token;
                           }
                       }
                       return token;
                   })
                   .filter(token -> token != null && token.startsWith("Bearer "))
                   .map(token -> token.substring(7))
                   .map(BearerToken::new);
    }
}
