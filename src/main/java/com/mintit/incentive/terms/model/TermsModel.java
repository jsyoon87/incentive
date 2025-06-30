package com.mintit.incentive.terms.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Pattern;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class TermsModel {

    @NotNull(groups={TermsChoice.class}, message="필수 필드 입니다")
    @NotBlank(groups={TermsChoice.class}, message="해당 필드는 공백일수 없습니다")
    private String termsId;

    @NotNull(groups={TermsChoice.class}, message="필수 필드 입니다")
    @NotBlank(groups={TermsChoice.class}, message="해당 필드는 공백일수 없습니다")
    @Pattern(regexp="[Y|N]", message="Y 또는 N 이어야 합니다")
    private String agree;

    @NotNull(groups={TermsCreate.class}, message="필수 필드 입니다")
    @NotBlank(groups={TermsCreate.class}, message="해당 필드는 공백일수 없습니다")
    private String title;

    @NotNull(groups={TermsCreate.class}, message="필수 필드 입니다")
    @NotBlank(groups={TermsCreate.class}, message="해당 필드는 공백일수 없습니다")
    @Pattern(regexp="[Y|N]", message="Y 또는 N 이어야 합니다")
    private String required;

    private String userGroup;

    @Null(groups={TermsChoice.class})
    private String regId;

    @Null(groups={TermsCreate.class})
    private String userUuid;
}
