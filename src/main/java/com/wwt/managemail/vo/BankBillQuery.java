package com.wwt.managemail.vo;

import lombok.Data;

@Data
public class BankBillQuery {
    private Integer bankCardId;
    private int[] transactionTypes;
    private String startTime;
    private String endTime;
}
