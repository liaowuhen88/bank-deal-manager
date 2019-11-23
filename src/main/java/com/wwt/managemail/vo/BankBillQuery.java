package com.wwt.managemail.vo;

import lombok.Data;

import java.util.Date;

@Data
public class BankBillQuery {
    private Integer bankCardId;
    private int[] transactionTypes;
    private Date startTime;
    private Date endTime;
}
