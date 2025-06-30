package com.mintit.incentive.terms.repository;

import com.mintit.incentive.common.repository.CustomRepository;
import com.mintit.incentive.terms.entity.TermsHistoryEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface TermsHistoryRepository
    extends R2dbcRepository<TermsHistoryEntity, Long>, CustomRepository<TermsHistoryEntity> {

    Mono<TermsHistoryEntity> findByTermsId(String termsId);

    @Query("SELECT * FROM iv_terms_h WHERE terms_id = :termsId AND user_uuid = :userUuid ORDER BY reg_dt DESC LIMIT 1")
    Mono<TermsHistoryEntity> selectTermsLast(String termsId, String userUuid);
}
