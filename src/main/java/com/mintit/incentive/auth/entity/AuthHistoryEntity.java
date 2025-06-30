package com.mintit.incentive.auth.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("iv_auth_h")
@NoArgsConstructor
public class AuthHistoryEntity implements Persistable<Integer> {

    @Id
    @Column(value="auth_id")
    private int authId;

    @Column(value="auth_key")
    private String authKey;

    @Column(value="status")
    private String status;


    @Column(value="type")
    private String type;

    @Column(value="reg_dt")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime regDt;

    @Column(value="upd_dt")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updDt;


    @Override
    public Integer getId() {
        return this.authId;
    }

    @Override
    public boolean isNew() {
        return this.regDt == null;
    }
}
