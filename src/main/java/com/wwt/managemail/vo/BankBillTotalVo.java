package com.wwt.managemail.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BankBillTotalVo {

    private BigDecimal totalTransactionAmount;
    private Integer transactionType;

    private String time;


}