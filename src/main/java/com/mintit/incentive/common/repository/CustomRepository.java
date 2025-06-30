package com.mintit.incentive.common.repository;

import java.util.LinkedHashMap;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CustomRepository<T> {

    public Flux<T> findAll(Class<T> entityClass, Pageable pageable);

    public Mono<Long> totalCount(Class<T> entityClass);

    public Flux<T> findAll(Class<T> entityClass, LinkedHashMap<String, Object> queryParams);

    public Flux<T> findAll(Class<T> entityClass, LinkedHashMap<String, Object> queryParams, Sort sort);

    public Flux<T> findAll(Class<T> entityClass, LinkedHashMap<String, Object> queryParams, Pageable pageable);

    public Flux<T> findAll(Class<T> entityClass, LinkedHashMap<String, Object> queryParams, Pageable pageable, Sort sort);

    public Mono<Long> totalCount(Class<T> entityClass, LinkedHashMap<String, Object> queryParams);

}
