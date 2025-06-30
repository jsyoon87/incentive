package com.mintit.incentive.incentive.model;

import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class IncentiveModel {

    @NotNull(groups={IncentiveCreate.class}, message="필수 필드 입니다")
    @NotBlank(groups={IncentiveCreate.class}, message="해당 필드는 공백일수 없습니다")
    private String imei;

    @NotNull(groups={IncentiveCreate.class}, message="필수 필드 입니다")
    @NotBlank(groups={IncentiveCreate.class}, message="해당 필드는 공백일수 없습니다")
    private String modelNm;

    @NotNull(groups={IncentiveWithdrawal.class}, message="필수 필드 입니다")
    private Object isAllChecked;

    @NotNull(groups={IncentiveWithdrawal.class}, message="필수 필드 입니다")
    private List<?> list;

    private String userUuid;
}
