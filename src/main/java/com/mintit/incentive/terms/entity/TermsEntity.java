package com.mintit.incentive.terms.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mintit.incentive.common.util.CommonUtil;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("iv_terms_m")
@NoArgsConstructor
public class TermsEntity implements Persistable<String> {

    @Id
    @Column(value="terms_id")
    private String termsId;

    @Column(value="title")
    private String title;

    @Column(value="url")
    private String url;

    @Column(value="required")
    private String required;

    @Column(value="user_group")
    private String userGroup;

    @Column(value="reg_id")
    private String regId;

    @Column(value="end_dt")
    private String endDt;
    @CreatedDate
    @Column(value="reg_dt")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime regDt;

    @Override
    public String getId() {
        return this.termsId;
    }

    @Override
    public boolean isNew() {
        boolean isNew = false;
        if (regDt == null) {
            this.termsId = CommonUtil.shortUUID();
            isNew = true;
        }
        return isNew;
    }
}
