package com.mintit.incentive.auth.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@RequiredArgsConstructor
public class AuthModel {

    @NotNull(groups={AuthPassword.class})
    @NotBlank(groups={AuthPassword.class})
    private String userUuid;

    @NotNull(groups={AuthLogin.class, AuthCiValid.class})
    @NotBlank(groups={AuthLogin.class, AuthCiValid.class})
    private String ci;//사용자 아이디

    private String accessToken; //인증 토큰

    @NotNull(groups={AuthRefreshToken.class})
    @NotBlank(groups={AuthRefreshToken.class})
    private String refreshToken; //재발급 토큰

    @NotNull(groups={AuthPassword.class})
    @NotBlank(groups={AuthPassword.class})
    private String authKey;

}
