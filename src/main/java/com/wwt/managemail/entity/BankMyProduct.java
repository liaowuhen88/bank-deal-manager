package com.wwt.managemail.entity;

import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import java.util.Date;

@Data
public class BankMyProduct {
    @Id
    @KeySql(useGeneratedKeys = true)
    private Long id;
    private Integer bankCardId;
    private Integer bankProductId;
    private Long investmentAmount;
    private Long interestRate;
    private Date profitDate;
    private Date depositPeriod;
    //private Long totalEffectiveUnterestIncome;
    private Date buyingTime;
    private Date dueTime;
    private String creator;
    private Date createTime;
    private String remark;
}