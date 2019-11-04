package com.wwt.managemail.vo;

import com.wwt.managemail.enums.TransactionTypeEnum;
import com.wwt.managemail.utils.MoneyUtills;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class BankBillVo {
    private Long id;
    private Long bankCardId;

    private BigDecimal transactionAmount;
    private Integer transactionType;

    private Date transactionTime;
    private String creator;

    private Date createTime;

    private String remark;

    private String name;

    private String bankName;

    private String bankCard;

    public String getTransactionTypeMsg() {
        return TransactionTypeEnum.getByCode(transactionType).getMsg();
    }

    public BigDecimal getRealTransactionAmount() {
        return MoneyUtills.getRealMoney(transactionType, transactionAmount);
    }
}