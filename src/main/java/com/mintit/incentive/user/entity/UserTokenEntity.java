package com.mintit.incentive.user.entity;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("iv_user_token_m")
@NoArgsConstructor
public class UserTokenEntity implements Persistable<String> {

    @Id
    @Column(value="user_uuid")
    private String userUuid;

    @Column(value="refresh_token")
    private String refreshToken;

    @CreatedDate
    @Column(value="last_dt")
    private LocalDateTime lastDt;

    @Override
    public String getId() {
        return userUuid;
    }

    @Override
    public boolean isNew() {
        return lastDt == null;
    }
}
