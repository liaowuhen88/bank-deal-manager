package com.wwt.managemail.entity;

import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import java.util.Date;

@Data
public class BankProduct {
    @Id
    @KeySql(useGeneratedKeys = true)
    private Integer id;
    private String bank;

    private String productType;

    private String bankProduct;

    private Long expectedinterestRate;

    private String interestPaymentMethod;

    private Integer depositPeriod;
    private String remark;

    private String creator;
    private Date creatorTime;
}