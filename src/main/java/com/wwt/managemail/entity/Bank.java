package com.wwt.managemail.entity;

import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;
@Table(name = "bank")
@Data
public class Bank {
    @Id
    @KeySql(useGeneratedKeys = true)
    private Integer id;
    private String name;
    private String bankName;
    private String bankCard;

    private BigDecimal cashAmount;
    private BigDecimal investmentAmount;

    private BigDecimal accountBalance;

    private String creator;

    private Date createTime;

    private String updator;

    private Date updateTime;

    public BigDecimal getAccountBalance() {
        if (null == cashAmount) {
            return investmentAmount;
        }
        return cashAmount.add(investmentAmount);
    }

    public String getSelectName() {
        return getBankName() + "__" + getName() + "__" + getBankCard();
    }
}