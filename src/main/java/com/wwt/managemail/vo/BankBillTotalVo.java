package com.wwt.managemail.vo;

import com.wwt.managemail.enums.TransactionTypeEnum;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class BankBillTotalVo {

    private BigDecimal totalTransactionAmount;
    private Integer transactionType;

    private String time;


    public String getTransactionTypeString() {
        return TransactionTypeEnum.getByCode(transactionType).getEnCode();
    }

}