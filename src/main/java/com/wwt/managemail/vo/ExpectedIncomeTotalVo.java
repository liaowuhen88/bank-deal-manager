package com.wwt.managemail.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ExpectedIncomeTotalVo {
    private String profitDate;
    private BigDecimal expectedInterestIncomeMonth;
}
