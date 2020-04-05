package com.wwt.managemail.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wwt.managemail.entity.BankBill;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class ProductTransaction extends BankBill {
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date nextProfitDate;
    /**
     * 部分赎回时，赎回本金金额
     */
    private BigDecimal investmentAmount;

}
