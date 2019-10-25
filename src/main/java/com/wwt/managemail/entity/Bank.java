package com.wwt.managemail.entity;

import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;
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

    private Long cashAmount;
    private Long investmentAmount;

    private Long accountBalance;

    private String creator;

    private Date createTime;

    private String updator;

    private Date updateTime;

    public Long getAccountBalance() {
        return (cashAmount==null?0:cashAmount)+(investmentAmount==null?0:investmentAmount);
    }
}