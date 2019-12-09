package com.wwt.managemail.vo;

import lombok.Data;

import java.util.Date;

@Data
public class BankMyProductQueryVO extends BankQueryVO {
    /**
     * 到期产品时间
     */
    private Date expireProductTime;

    /**
     * 到期利息时间
     */
    private Date expireInterestTime;

    private Integer state;
}
