package com.wwt.managemail.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PageHomeVo {
    /**
     * 现金金额
     */
    private BigDecimal cashAmount;
    /**
     * 投资金额
     */
    private BigDecimal investmentAmount;

    private BigDecimal accountBalance;


}
