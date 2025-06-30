package com.mintit.incentive.notice.model;

import com.mintit.incentive.common.annotation.DateValid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class NoticeModel {


    @NotNull(groups={NoticeCreate.class}, message="필수 필드 입니다")
    @NotBlank(groups={NoticeCreate.class}, message="해당 필드는 공백일수 없습니다")
    private String noticeType;

    @NotNull(groups={NoticeCreate.class}, message="필수 필드 입니다")
    @NotBlank(groups={NoticeCreate.class}, message="해당 필드는 공백일수 없습니다")
    private String title;

    @NotNull(groups={NoticeCreate.class}, message="필수 필드 입니다")
    @NotBlank(groups={NoticeCreate.class}, message="해당 필드는 공백일수 없습니다")
    private String contents;
    
    private String userGroup;

    @NotNull(groups={NoticeCreate.class}, message="필수 필드 입니다")
    @NotBlank(groups={NoticeCreate.class}, message="해당 필드는 공백일수 없습니다")
    @DateValid(groups={NoticeCreate.class})
    private String startDt;

    @NotNull(groups={NoticeCreate.class}, message="필수 필드 입니다")
    @NotBlank(groups={NoticeCreate.class}, message="해당 필드는 공백일수 없습니다")
    @DateValid(groups={NoticeCreate.class})
    private String endDt;

    private String regId;
}
