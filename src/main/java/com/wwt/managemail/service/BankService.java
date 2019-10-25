package com.wwt.managemail.service;

import com.wwt.managemail.entity.Bank;
import com.wwt.managemail.entity.BankBill;

import java.util.List;

public interface BankService {
    /**
     * 保存银行卡信息
     * @param bank
     * @return
     */
    int insert(Bank bank);

    int transaction(BankBill bankBill);

    List<Bank> selectAll();
}
