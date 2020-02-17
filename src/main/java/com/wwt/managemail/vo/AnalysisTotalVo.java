package com.wwt.managemail.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AnalysisTotalVo {

    private String time;
    /**
     * 预期利息收入
     */
    private BigDecimal expectedInterestIncome;
    /**
     * 理财利息收入
     */
    private BigDecimal investmentIncome;
    /**
     * 投资金额
     */
    private BigDecimal investmentAmount;
    /**
     * 赎回本金
     */
    private BigDecimal investmentRedeemPrincipal;

}
