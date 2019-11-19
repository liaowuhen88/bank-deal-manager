package com.wwt.managemail.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wwt.managemail.entity.BankBill;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class ProductIncome extends BankBill {
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date nextProfitDate;
}
