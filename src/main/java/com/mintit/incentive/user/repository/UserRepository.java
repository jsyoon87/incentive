package com.mintit.incentive.user.repository;


import com.mintit.incentive.common.repository.CustomRepository;
import com.mintit.incentive.user.entity.UserEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface UserRepository
    extends R2dbcRepository<UserEntity, Long>, CustomRepository<UserEntity> {

    public Mono<UserEntity> findByUserUuid(String userUuid);

    public Mono<UserEntity> findByCi(String ci);

    public Mono<Long> deleteByCi(String ci);
}

