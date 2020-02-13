package com.wwt.managemail.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date transactionTime;
    private String creator;

    private Date createTime;

    private String remark;

    private String name;

    private String bankName;

    private String bankCard;

    private String myProductId;

    private String transferCard;

    private String transferCardName;

    private String transferCardNum;

    private String transferCardBankName;

    public String getTransactionTypeMsg() {
        return TransactionTypeEnum.getByCode(transactionType).getMsg();
    }

    public BigDecimal getRealTransactionAmount() {
        return MoneyUtills.getRealMoney(transactionType, transactionAmount);
    }
}