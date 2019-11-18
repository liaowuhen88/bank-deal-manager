package com.wwt.managemail.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;
@Table(name = "bank_bill")
@Data
public class BankBill {
    @Id
    @KeySql(useGeneratedKeys = true)
    private Long id;
    private Integer bankCardId;
    private Integer myProductId;
    private Integer transferCard;

    private BigDecimal transactionAmount;
    private Integer transactionType;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date transactionTime;
    private String creator;

    private Date createTime;

    private String remark;

    }