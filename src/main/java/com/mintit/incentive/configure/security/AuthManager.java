package com.mintit.incentive.configure.security;

import com.mintit.incentive.common.exception.NotLoginException;
import com.mintit.incentive.common.exception.UnauthorizationException;
import com.mintit.incentive.common.util.JWTUtil;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
public class AuthManager implements ReactiveAuthenticationManager {

    final JWTUtil jwtUtil;

    final ReactiveUserDetailsService reactiveUserDetailsService;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        return Mono.justOrEmpty(authentication).cast(BearerToken.class).flatMap(auth -> {
            String getUserId = jwtUtil.setJwtParser("jwt").getLoginUuidByJwt(auth.getCredentials());
            Mono<UserDetails> foundUser = reactiveUserDetailsService.findByUsername(getUserId)
                                                                    .defaultIfEmpty(new CustomUserDetails());
            return foundUser.flatMap(user -> {
                if (user.getUsername() == null) {
                    return Mono.error(new NotLoginException());
                }
                if (jwtUtil.validateJwtToken(user, auth.getCredentials())) {
                    return Mono.justOrEmpty(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword(), user.getAuthorities()));
                }
                return Mono.error(new UnauthorizationException("유효하지 않는 토큰 정보 입니다"));
            });
        });

    }
}
