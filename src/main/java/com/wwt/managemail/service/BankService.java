package com.wwt.managemail.service;

import com.wwt.managemail.entity.Bank;
import com.wwt.managemail.entity.BankBill;
import com.wwt.managemail.vo.BankQueryVO;

import java.util.List;

public interface BankService {
    Bank selectById(Integer id);
    /**
     * 保存银行卡信息
     * @param bank
     * @return
     */
    int insertSelective(Bank bank);

    int transaction(BankBill bankBill);

    List<Bank> selectAll();

    List<Bank> select(BankQueryVO bankQueryVO);

}
