package com.mintit.incentive.user.repository;

import com.mintit.incentive.user.entity.UserTokenEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface UserTokenRepository extends R2dbcRepository<UserTokenEntity, Long> {

    public Mono<UserTokenEntity> findByUserUuid(String userUuid);

    public Mono<UserTokenEntity> findByUserUuidAndRefreshToken(String userUuid, String refreshToken);

}

