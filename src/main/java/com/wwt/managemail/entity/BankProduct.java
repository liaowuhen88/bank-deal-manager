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
    /**
     * 银行
     */
    private String bank;
    /**
     * 产品类型
     */
    private String productType;
    /**
     * 产品
     */
    private String bankProduct;
    /**
     * 预期利率
     */
    private Long expectedinterestRate;
    /**
     * 付息方式
     */
    private String interestPaymentMethod;
    /**
     * 存款期（日）
     */
    private Integer depositPeriod;
    /**
     * 备注
     */
    private String remark;

    private String creator;
    private Date creatorTime;

    public String getSelectName() {
        return productType + "_" + bankProduct;
    }
}