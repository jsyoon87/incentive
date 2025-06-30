package com.mintit.incentive.incentive.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("iv_incentive_withdrawal_m")
@NoArgsConstructor
public class IncentiveWithdrawalEntity implements Persistable<Integer> {

    @Id
    @Column(value="withdrawal_id")
    private int withdrawalId;

    @Column(value="incentive_id")
    private String incentiveId;

    @Column(value="status")
    private String status;

    @Column(value="amount")
    private int amount;

    @Column(value="account_no")
    private String accountNo;

    @Column(value="bank_cd")
    private String bankCd;

    @Column(value="reg_id")
    private String regId;

    @Column(value="reg_dt")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime regDt;

    @Override
    public Integer getId() {
        return this.withdrawalId;
    }

    @Override
    public boolean isNew() {
        return this.regDt == null;
    }
}
