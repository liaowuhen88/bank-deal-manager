package com.wwt.managemail.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import java.util.Date;

@Data
public class BankMyProduct {
    @Id
    @KeySql(useGeneratedKeys = true)
    private Long id;
    /**
     * 银行账户
     */
    private Integer bankCardId;
    /**
     * 产品号
     */
    private Integer bankProductId;
    /**
     * 投资金额
     */
    private Long investmentAmount;
    /**
     * 实际利率
     */
    private Long interestRate;
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
    //private Long totalEffectiveUnterestIncome;
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
    private String creator;
    private Date createTime;
    private String remark;
}