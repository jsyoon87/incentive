package com.mintit.incentive.notice.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("iv_notice_m")
@NoArgsConstructor
public class NoticeEntity implements Persistable<Integer> {

    @Id
    @Column(value="notice_id")
    private int noticeId;

    @Column(value="notice_type")
    private String noticeType;

    @Column(value="title")
    private String title;

    @Column(value="contents")
    private String contents;

    @Column(value="user_group")
    private String userGroup;

    @Column(value="reg_id")
    private String regId;
    @CreatedDate
    @Column(value="reg_dt")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime regDt;

    @Column(value="upd_id")
    private String updId;

    @Column(value="upd_dt")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updDt;

    @Column(value="start_dt")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startDt;

    @Column(value="end_dt")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endDt;

    @Override
    public Integer getId() {
        return this.noticeId;
    }

    @Override
    public boolean isNew() {
        return this.regDt == null;
    }
}
