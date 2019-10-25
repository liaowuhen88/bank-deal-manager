package com.wwt.managemail.vo;

import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
public class BankBillVo {
    private Long id;
    private Long bankCardId;

    private Long transactionAmount;
    private String transactionType;

    private Date transactionTime;
    private String creator;

    private Date createTime;

    private String remark;

    private String name;

    private String bankName;

    private String bankCard;

    }