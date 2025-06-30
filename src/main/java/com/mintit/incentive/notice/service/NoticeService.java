package com.mintit.incentive.notice.service;


import com.mintit.incentive.common.util.ConvertUtil;
import com.mintit.incentive.notice.entity.NoticeEntity;
import com.mintit.incentive.notice.model.NoticeModel;
import com.mintit.incentive.notice.repository.NoticeRepository;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class NoticeService {

    final private NoticeRepository noticeRepository;

    final private ConvertUtil convertUtil;

    public Flux<NoticeEntity> list(LinkedHashMap<String, Object> queryParam, Pageable pageable) {
        return this.noticeRepository.findAll(NoticeEntity.class, queryParam, pageable, Sort.by(Order.desc("reg_dt")));
    }

    public Mono<Long> totalCount(LinkedHashMap<String, Object> queryParam) {
        return this.noticeRepository.totalCount(NoticeEntity.class, queryParam);
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

    public Mono<NoticeEntity> create(NoticeModel noticeModel) {
        NoticeEntity noticeEntity = this.convertUtil.convert(noticeModel, NoticeEntity.class);
        noticeEntity.setStartDt(LocalDate.parse(noticeModel.getStartDt(), DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                                         .atStartOfDay());
        noticeEntity.setEndDt(LocalDate.parse(noticeModel.getEndDt(), DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                                       .atStartOfDay()
                                       .plusDays(1)
                                       .minusSeconds(1));

        return this.noticeRepository.save(noticeEntity);
    }


}
