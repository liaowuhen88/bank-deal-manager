package com.wwt.managemail.entity;

import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;
@Table(name = "bank_bill")
@Data
public class BankBill {
    @Id
    @KeySql(useGeneratedKeys = true)
    private Long id;
    private Long bankCardId;

    private Long transactionAmount;
    private String transactionType;

    private Date transactionTime;
    private String creator;

    private Date createTime;

    private String remark;

    }