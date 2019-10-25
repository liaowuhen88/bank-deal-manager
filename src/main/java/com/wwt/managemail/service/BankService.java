package com.wwt.managemail.service;

import com.wwt.managemail.entity.Bank;

import java.util.List;

public interface BankService {
    /**
     * 保存银行卡信息
     * @param bank
     * @return
     */
    int insert(Bank bank);

    List<Bank> selectAll();
}
