package com.wwt.managemail.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class BankMyProduct {
    @Id
    @KeySql(useGeneratedKeys = true)
    private Integer id;
    /**
     * 银行账户
     */
    private Integer bankCardId;
    /**
     * 投资金额
     */
    private BigDecimal investmentAmount;
    /**
     * 实际利率
     */
    private BigDecimal interestRate;
    /**
     * 利息预期收益(月)
     */
    private BigDecimal expectedInterestIncomeMonth;
    /**
     * 本息收益
     */
    private BigDecimal principalAndInterestIncome;

    /**
     * 收利日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date profitDate;
    /**
     * 存款期（日）
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date depositPeriod;
    /**
     * 实际利息总收益
     */
    private BigDecimal totalEffectiveInterestIncome;
    /**
     * buyingTime
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date buyingTime;
    /**
     * 到期时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date dueTime;
    /**
     * 产品类型
     */
    private String productType;
    /**
     * 预期利率
     */
    private BigDecimal expectedInterestRate;
    /**
     * 利息预期收益
     */
    private BigDecimal expectedInterestIncomeTotal;
    /**
     * 付息方式
     */
    private String interestPaymentMethod;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date interestStartTime;
    private String creator;
    private Date createTime;
    private String remark;
}