package com.mintit.incentive.terms.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("iv_terms_h")
@NoArgsConstructor
public class TermsHistoryEntity implements Persistable<Integer> {

    @Id
    @Column(value="terms_history_id")
    private int termsHistoryId;

    @Column(value="terms_id")
    private String termsId;

    @Column(value="user_uuid")
    private String userUuid;

    @Column(value="agree")
    private String agree;

    @Column(value="reg_dt")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime regDt;

    @Override
    public Integer getId() {
        return this.termsHistoryId;
    }

    @Override
    public boolean isNew() {
        return this.regDt == null;
    }
}
