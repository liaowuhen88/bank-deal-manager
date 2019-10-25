package com.wwt.managemail.vo;

import lombok.Data;

@Data
public class BankBillQuery {
    private Long bankCardId;
    private String transactionType;
}
