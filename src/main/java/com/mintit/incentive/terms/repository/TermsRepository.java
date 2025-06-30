package com.mintit.incentive.terms.repository;

import com.mintit.incentive.common.repository.CustomRepository;
import com.mintit.incentive.terms.entity.TermsEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface TermsRepository extends R2dbcRepository<TermsEntity, Long>,
    CustomRepository<TermsEntity> {

    Mono<TermsEntity> findByTermsId(String termsId);
}
