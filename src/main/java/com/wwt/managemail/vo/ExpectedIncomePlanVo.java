package com.wwt.managemail.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
/**
 * 预期收息计划表
 */
public class ExpectedIncomePlanVo {
    /**
     * 收息日期
     */
    private String time;
    /**
     * 收息金额
     */
    private BigDecimal expectedInterestIncomeMonth;

    /**
     * 收息金额
     */
    private BigDecimal realInterestIncome;
    /**
     * 产品id
     */
    private Integer id;
    /**
     * 付息方式
     */
    private String interestPaymentMethod;
}
