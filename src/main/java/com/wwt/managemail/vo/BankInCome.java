package com.wwt.managemail.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class BankInCome {
    @NotNull(message = "银行卡id不能为空")
    private Integer bankCardId;
    @NotNull(message = "交易金额不能为空")
    private Long amount;
}
