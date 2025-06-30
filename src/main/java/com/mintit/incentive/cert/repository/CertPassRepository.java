package com.mintit.incentive.cert.repository;

import com.mintit.incentive.cert.entity.CertPassEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface CertPassRepository extends R2dbcRepository<CertPassEntity, Long> {

    public Mono<CertPassEntity> findByReqSeq(String reqSeq);
}

