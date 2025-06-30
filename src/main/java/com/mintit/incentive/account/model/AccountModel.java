package com.mintit.incentive.account.model;


import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class AccountModel {

    @NotNull
    @NotBlank
    private String accountNo;

    @NotNull
    @NotBlank
    private String bankCode;
}
