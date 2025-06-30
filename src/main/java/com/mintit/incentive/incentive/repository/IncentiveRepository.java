package com.mintit.incentive.incentive.repository;

import com.mintit.incentive.common.repository.CustomRepository;
import com.mintit.incentive.incentive.entity.IncentiveEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface IncentiveRepository
    extends R2dbcRepository<IncentiveEntity, Long>, CustomRepository<IncentiveEntity> {

    public Mono<IncentiveEntity> findByIncentiveId(String incentiveId);

    @Query("SELECT IFNULL(SUM(amount),0) AS amount FROM iv_incentive_m WHERE user_uuid = :userUuid AND status = :status")
    public Mono<Long> selectTotalAmount(String userUuid, String status);

    @Query("SELECT COUNT(*) FROM iv_incentive_m WHERE user_uuid = :userUuid AND status = :status")
    public Mono<Long> selectTotalCount(String userUuid, String status);

}
