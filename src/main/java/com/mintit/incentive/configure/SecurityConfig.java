package com.mintit.incentive.configure;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mintit.incentive.common.type.ResponseType;
import com.mintit.incentive.configure.security.AuthConverter;
import com.mintit.incentive.configure.security.AuthManager;
import com.mintit.incentive.configure.security.CustomUserDetails;
import com.mintit.incentive.user.repository.UserRepository;
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * WebFlux Security 정의
 */
@Configuration
@AllArgsConstructor
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfig {

    private UserRepository authRepository;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ReactiveUserDetailsService userDetailsService() {
        return userId -> this.authRepository.findByUserUuid(userId).flatMap(v -> {
            CustomUserDetails customUserDetails = new CustomUserDetails();
            customUserDetails.setUsername(v.getUserUuid());
            return Mono.just(customUserDetails);
        });

    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http, AuthConverter authConverter, AuthManager authManager) {
        AuthenticationWebFilter filter = new AuthenticationWebFilter(authManager);
        filter.setServerAuthenticationConverter(authConverter);
        return http.authorizeExchange(auth -> auth.pathMatchers(HttpMethod.OPTIONS)
                                                  .permitAll()
                                                  .pathMatchers("/auth/**")
                                                  .permitAll()
                                                  .pathMatchers("/cert/**")
                                                  .permitAll()
                                                  .pathMatchers(HttpMethod.POST, "/users")
                                                  .permitAll()
                                                  .pathMatchers(HttpMethod.GET, "/terms")
                                                  .permitAll()
                                                  .pathMatchers(HttpMethod.POST, "/terms/choice")
                                                  .permitAll()
                                                  .anyExchange()
                                                  .authenticated())
                   .addFilterAt(filter, SecurityWebFiltersOrder.AUTHENTICATION)
                   .httpBasic()
                   .disable()
                   .formLogin()
                   .disable()
                   .exceptionHandling()
                   .authenticationEntryPoint((swe, e) -> {
                       swe.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                       swe.getResponse()
                          .getHeaders()
                          .add(HttpHeaders.CONTENT_TYPE, String.valueOf(MediaType.APPLICATION_JSON));
                       ObjectMapper mapper = new ObjectMapper();
                       Map<String, Object> errorMap = new HashMap<>();
                       errorMap.put("result", new HashMap<String, String>() {{
                           put("message", "유효하지 않은 접근 입니다");
                       }});
                       errorMap.put("status", ResponseType.UNAUTHORIZATION.code());
                       DataBuffer buffer = null;
                       try {
                           buffer = swe.getResponse()
                                       .bufferFactory()
                                       .wrap(mapper.writeValueAsBytes(errorMap));
                       } catch (JsonProcessingException ex) {
                           throw new RuntimeException(ex);
                       }
                       return swe.getResponse().writeWith(Flux.just(buffer));
                   })
                   .accessDeniedHandler((swe, e) -> {
                       swe.getResponse().setStatusCode(HttpStatus.NOT_FOUND);
                       swe.getResponse()
                          .getHeaders()
                          .add(HttpHeaders.CONTENT_TYPE, String.valueOf(MediaType.APPLICATION_JSON));
                       ObjectMapper mapper = new ObjectMapper();
                       Map<String, Object> errorMap = new HashMap<>();
                       errorMap.put("result", "유효하지 않은 접근 입니다");
                       errorMap.put("status", ResponseType.NOT_FOUND.code());
                       DataBuffer buffer = null;
                       try {
                           buffer = swe.getResponse()
                                       .bufferFactory()
                                       .wrap(mapper.writeValueAsBytes(errorMap));
                       } catch (JsonProcessingException ex) {
                           throw new RuntimeException(ex);
                       }
                       return swe.getResponse().writeWith(Flux.just(buffer));
                   })
                   .and()
                   .csrf()
                   .disable()
                   .build();
    }
}
