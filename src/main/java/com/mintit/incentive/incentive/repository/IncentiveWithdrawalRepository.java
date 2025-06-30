package com.mintit.incentive.incentive.repository;

import com.mintit.incentive.common.repository.CustomRepository;
import com.mintit.incentive.incentive.entity.IncentiveWithdrawalEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface IncentiveWithdrawalRepository extends
    R2dbcRepository<IncentiveWithdrawalEntity, Long>, CustomRepository<IncentiveWithdrawalEntity> {

    public Mono<IncentiveWithdrawalEntity> findByIncentiveId(String incentiveId);
}
