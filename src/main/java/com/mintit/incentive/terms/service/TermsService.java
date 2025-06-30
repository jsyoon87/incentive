package com.mintit.incentive.terms.service;

import com.mintit.incentive.common.util.ConvertUtil;
import com.mintit.incentive.terms.entity.TermsEntity;
import com.mintit.incentive.terms.entity.TermsHistoryEntity;
import com.mintit.incentive.terms.model.TermsModel;
import com.mintit.incentive.terms.repository.TermsHistoryRepository;
import com.mintit.incentive.terms.repository.TermsRepository;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class TermsService {

    final private TermsRepository termsRepository;

    final private TermsHistoryRepository termsHistoryRepository;

    final private ConvertUtil convertUtil;

    public Flux<TermsEntity> list(LinkedHashMap<String, Object> queryParam, Pageable pageable) {
        return this.termsRepository.findAll(TermsEntity.class, queryParam, pageable)
                                   .flatMap(result -> {
                                       result.setEndDt(LocalDate.parse(result.getEndDt(), DateTimeFormatter.ofPattern("yyyyMMdd"))
                                                                .atStartOfDay()
                                                                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

                                       return Mono.just(result);
                                   });
    }


    public Mono<Map<String, Object>> getPageList(LinkedHashMap<String, Object> queryParam, Pageable pageable) {
        return this.totalCount(queryParam)
                   .flatMap(count -> this.list(queryParam, pageable)
                                         .flatMap(terms -> Mono.just(this.convertUtil.toMap(terms)))
                                         .collectList()
                                         .flatMap(termsList -> {
                                             Map<String, Object> result = new HashMap<>();
                                             result.put("totalCount", count);
                                             result.put("list", termsList);
                                             return Mono.just(result);
                                         }));
    }

    public Mono<Long> totalCount(LinkedHashMap<String, Object> queryParam) {
        return this.termsRepository.totalCount(TermsEntity.class, queryParam);
    }

    public Mono<TermsEntity> create(TermsModel termsModel) {
        TermsEntity termsEntity = this.convertUtil.convert(termsModel, TermsEntity.class);
        termsEntity.setUrl("S3 업로드후 전달받은 URL 입력하여 저장");
        return this.termsRepository.save(termsEntity).flatMap(terms -> {
            return this.termsRepository.findByTermsId(terms.getTermsId());
        });
    }

    public Mono<String> choice(TermsModel termsModel) {
        TermsHistoryEntity termsHistoryEntity = this.convertUtil.convert(termsModel, TermsHistoryEntity.class);
        return this.termsRepository.findByTermsId(termsModel.getTermsId()).flatMap(terms -> {
            return this.termsHistoryRepository.save(termsHistoryEntity)
                                              .flatMap(v -> Mono.just("정상적으로 변경 하였습니다"))
                                              .doOnError(throwable -> log.error("terms check error", throwable))
                                              .onErrorReturn("관리자에게 문의하세요");
        }).switchIfEmpty(Mono.defer(() -> Mono.just("등록되지 않은 약관 정보입니다")));

    }
}
