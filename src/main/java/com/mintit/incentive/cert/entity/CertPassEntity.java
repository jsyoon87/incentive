package com.mintit.incentive.cert.entity;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("iv_cert_pass_h")
@NoArgsConstructor
public class CertPassEntity implements Persistable<String> {

    @Id
    @Column(value="req_seq")
    private String reqSeq;

    @Column(value="err_code")
    private String errCode;

    @Column(value="name")
    private String name;

    @Column(value="message")
    private String message;

    @Column(value="mobile_no")
    private String mobileNo;

    @Column(value="mobile_co")
    private String mobileCo;

    @Column(value="gender")
    private String gender;

    @Column(value="birthdate")
    private String birthdate;

    @Column(value="nationalinfo")
    private String nationalinfo;

    @Column(value="auth_type")
    private String authType;

    @Column(value="ci")
    private String ci;

    @CreatedDate
    @Column(value="reg_dt")
    private LocalDateTime regDt;

    @Override
    public String getId() {
        return reqSeq;
    }

    @Override
    public boolean isNew() {
        return regDt == null;
    }
}
