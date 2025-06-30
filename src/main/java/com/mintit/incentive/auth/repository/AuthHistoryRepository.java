package com.mintit.incentive.auth.repository;

import com.mintit.incentive.auth.entity.AuthHistoryEntity;
import com.mintit.incentive.common.repository.CustomRepository;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface AuthHistoryRepository extends R2dbcRepository<AuthHistoryEntity, Long>,
    CustomRepository<AuthHistoryEntity> {

    @Query("SELECT * FROM iv_auth_h WHERE auth_key = :authKey AND status = '0'")
    Mono<AuthHistoryEntity> selectAuthHistory(String authKey);
}
