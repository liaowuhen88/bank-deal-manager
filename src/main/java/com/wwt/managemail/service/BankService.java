package com.wwt.managemail.service;

import com.wwt.managemail.entity.Bank;
import com.wwt.managemail.vo.BankInCome;

import java.util.List;

public interface BankService {
    /**
     * 保存银行卡信息
     * @param bank
     * @return
     */
    int insert(Bank bank);

    /**
     * 收入
     * @param bankInCome
     * @return
     */
    int income(BankInCome bankInCome);

    List<Bank> selectAll();
}
