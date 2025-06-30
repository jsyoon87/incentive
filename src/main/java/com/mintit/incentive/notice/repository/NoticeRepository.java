package com.mintit.incentive.notice.repository;

import com.mintit.incentive.common.repository.CustomRepository;
import com.mintit.incentive.notice.entity.NoticeEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface NoticeRepository extends R2dbcRepository<NoticeEntity, Long>,
    CustomRepository<NoticeEntity> {}
