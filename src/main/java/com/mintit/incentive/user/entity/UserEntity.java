package com.mintit.incentive.user.entity;

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
@Table("iv_user_m")
@NoArgsConstructor
public class UserEntity implements Persistable<String> {

    @Id
    @Column(value="user_uuid")
    private String userUuid;

    @Column(value="ci")
    private String ci;

    @Column(value="user_nm")
    private String userNm;

    @Column(value="user_group")
    private String userGroup;

    @Column(value="mobile_no")
    private String mobileNo;

    @Column(value="mobile_co")
    private String mobileCo;

    @Column(value="gender")
    private String gender;

    @Column(value="birth_date")
    private String birthDate;

    @Column(value="account_no")
    private String accountNo;

    @Column(value="bank_cd")
    private String bankCd;

    @CreatedDate
    @Column(value="reg_dt")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime regDt;

    @Override
    public String getId() {
        return userUuid;
    }

    @Override
    public boolean isNew() {
        boolean isNew = false;
        if (regDt == null) {
            this.userUuid = CommonUtil.shortUUID();
            isNew = true;
        }
        return isNew;
    }
}
