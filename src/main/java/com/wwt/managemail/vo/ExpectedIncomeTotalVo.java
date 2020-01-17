package com.wwt.managemail.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class ExpectedIncomeTotalVo {
    private Date profitDate;
    private BigDecimal expectedInterestIncomeMonth;
}
