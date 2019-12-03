package com.wwt.managemail.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BankTotalVo {

    /**
     * 现金金额
     */
    private BigDecimal totalCashAmount;
    /**
     * 投资金额
     */
    private BigDecimal totalInvestmentAmount;
    /**
     * 总金额
     */
    private BigDecimal totalAccountBalance;


    public BigDecimal getTotalAccountBalance() {
        if (null == totalCashAmount) {
            return totalInvestmentAmount;
        }
        if (null == totalInvestmentAmount) {
            return totalCashAmount;
        }
        return totalCashAmount.add(totalInvestmentAmount);
    }
}