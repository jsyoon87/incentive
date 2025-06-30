package com.mintit.incentive.common.repository.impl;

import com.mintit.incentive.common.repository.CustomRepository;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class CustomRepositoryImpl<T> implements CustomRepository<T> {

    T targetClass;
    final private R2dbcEntityTemplate r2dbcEntityTemplate;


    @Override
    public Flux<T> findAll(Class<T> entityClass, Pageable pageable) {
        return this.findAll(entityClass, new LinkedHashMap<>(), pageable);
    }

    @Override
    public Mono<Long> totalCount(Class<T> entityClass) {
        return this.totalCount(entityClass, new LinkedHashMap<>());
    }

    @Override
    public Flux<T> findAll(Class<T> entityClass, LinkedHashMap<String, Object> queryParams) {
        return r2dbcEntityTemplate.select(entityClass).matching(this.getQuery(queryParams)).all();
    }

    @Override
    public Flux<T> findAll(Class<T> entityClass, LinkedHashMap<String, Object> queryParams, Sort sort) {
        return r2dbcEntityTemplate.select(entityClass)
                                  .matching(this.getQuery(queryParams).sort(sort))
                                  .all();
    }

    @Override
    public Flux<T> findAll(Class<T> entityClass, LinkedHashMap<String, Object> queryParams, Pageable pageable, Sort sort) {
        return r2dbcEntityTemplate.select(entityClass)
                                  .matching(this.getQuery(queryParams)
                                                .offset(pageable.getOffset())
                                                .limit(pageable.getPageSize())
                                                .sort(sort))
                                  .all();
    }

    @Override
    public Flux<T> findAll(Class<T> entityClass, LinkedHashMap<String, Object> queryParams, Pageable pageable) {
        return r2dbcEntityTemplate.select(entityClass)
                                  .matching(this.getQuery(queryParams)
                                                .offset(pageable.getOffset())
                                                .limit(pageable.getPageSize()))
                                  .all();
    }

    @Override
    public Mono<Long> totalCount(Class<T> entityClass, LinkedHashMap<String, Object> queryParams) {
        return r2dbcEntityTemplate.count(this.getQuery(queryParams), entityClass);
    }

    private Query getQuery(LinkedHashMap<String, Object> queryParams) {
        Query dynamicQuery;
        if (queryParams.keySet().size() == 0) {
            dynamicQuery = Query.empty();
        } else {
            Criteria criteria = null;
            int i = 0;
            for (Map.Entry<String, Object> e : queryParams.entrySet()) {
                if (e.getValue() instanceof List) {
                    List<?> queryMapList = (List<?>) e.getValue();
                    for (Object o : queryMapList) {
                        Map<?, ?> convertQueryMap = (Map<?, ?>) o;
                        criteria = this.getTypeQuery(criteria, i, e.getKey(), convertQueryMap);
                    }
                } else {
                    criteria = this.getTypeQuery(criteria, i, e.getKey(), e.getValue());
                }
                i++;
            }
            assert criteria != null;
            dynamicQuery = Query.query(criteria);
        }
        return dynamicQuery;
    }

    private Criteria getTypeQuery(Criteria criteria, int idx, String columnKey, Object columnValue) {
        boolean isWhere = idx == 0;
        if (columnValue instanceof Map) {
            HashMap<?, ?> mapQuery = (HashMap<?, ?>) columnValue;
            String type = (String) mapQuery.get("type");
            Object value = mapQuery.get("value");

            switch (type) {
                case "gte":
                    criteria = Criteria.from(isWhere
                                             ? Criteria.where(columnKey)
                                                       .greaterThanOrEquals(value)
                                             : criteria.and(columnKey).greaterThanOrEquals(value));
                    break;
                case "gt":
                    criteria = Criteria.from(isWhere
                                             ? Criteria.where(columnKey).greaterThan(value)
                                             : criteria.and(columnKey).greaterThan(value));
                    break;
                case "lt":
                    criteria = Criteria.from(isWhere
                                             ? Criteria.where(columnKey).lessThan(value)
                                             : criteria.and(columnKey).lessThan(value));
                    break;
                case "lte":
                    criteria = Criteria.from(isWhere
                                             ? Criteria.where(columnKey)
                                                       .lessThanOrEquals(value)
                                             : criteria.and(columnKey).lessThanOrEquals(value));
                    break;
                case "in":
                    Set<?> inQuery = new HashSet<>((Collection<?>) value);
                    criteria = Criteria.from(isWhere
                                             ? Criteria.where(columnKey).in(inQuery)
                                             : criteria.and(columnKey).in(inQuery));
                    break;
                case "notin":
                    Set<?> notInQuery = new HashSet<>((Collection<?>) value);
                    criteria = Criteria.from(isWhere
                                             ? Criteria.where(columnKey).notIn(notInQuery)
                                             : criteria.and(columnKey).notIn(notInQuery));
                    break;
                case "eq":
                    criteria = Criteria.from(isWhere
                                             ? Criteria.where(columnKey).is(value)
                                             : criteria.and(columnKey).is(value));
                    break;
            }

        } else {
            criteria = Criteria.from(isWhere
                                     ? Criteria.where(columnKey).is(columnValue)
                                     : criteria.and(columnKey).is(columnValue));
        }

        return criteria;
    }
}
