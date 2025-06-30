package com.mintit.incentive.incentive.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mintit.incentive.common.util.CommonUtil;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("iv_incentive_m")
@NoArgsConstructor
public class IncentiveEntity implements Persistable<String> {

    @Id
    @Column(value="incentive_id")
    private String incentiveId;

    @Column(value="imei")
    private String imei;

    @Column(value="status")
    private String status;

    @Column(value="amount")
    private int amount;

    @Column(value="model_nm")
    private String modelNm;

    @Column(value="user_uuid")
    private String userUuid;

    @Column(value="reg_dt")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime regDt;


    @Override
    public String getId() {
        return this.incentiveId;
    }

    @Override
    public boolean isNew() {
        boolean isNew = false;
        if (regDt == null) {
            this.incentiveId = CommonUtil.shortUUID();
            isNew = true;
        }
        return isNew;
    }
}
