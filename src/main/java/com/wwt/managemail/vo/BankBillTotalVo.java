package com.wwt.managemail.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class BankBillTotalVo {

    private BigDecimal totalTransactionAmount;
    private Integer transactionType;

    private Date createTime;


}