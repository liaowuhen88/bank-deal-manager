package com.wwt.managemail.vo;

import com.wwt.managemail.entity.Bank;
import com.wwt.managemail.entity.BankMyProduct;
import lombok.Data;

@Data
public class BankMyProductVo extends BankMyProduct {
    private Bank bank;
}
