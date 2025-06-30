package com.mintit.incentive.user.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class UserModel {

    @NotNull(groups={UserUpdate.class})
    @NotBlank(groups={UserUpdate.class})
    private String userUuid;

    @NotNull(groups={UserCreate.class})
    @NotBlank(groups={UserCreate.class})
    @Null(groups={UserUpdate.class})
    private String ci;

    @NotNull(groups={UserCreate.class})
    @NotBlank(groups={UserCreate.class})
    private String userNm;
    

    @NotNull(groups={UserCreate.class})
    @NotBlank(groups={UserCreate.class})
    @Null(groups={UserUpdate.class})
    private String userGroup;

    @NotNull(groups={UserCreate.class})
    @NotBlank(groups={UserCreate.class})
    private String mobileNo;

    @NotNull(groups={UserCreate.class})
    @NotBlank(groups={UserCreate.class})
    @Null(groups={UserUpdate.class})
    private String mobileCo;

    @NotNull(groups={UserCreate.class})
    @NotBlank(groups={UserCreate.class})
    private String gender;

    @NotNull(groups={UserCreate.class})
    @NotBlank(groups={UserCreate.class})
    private String birthDate;

    private String checkTerms;

    private String accountNo;

    private String bankCd;
}
