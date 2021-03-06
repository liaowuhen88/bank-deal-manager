package com.wwt.managemail.service;

import com.wwt.managemail.entity.Bank;
import com.wwt.managemail.entity.BankBill;
import com.wwt.managemail.vo.BankQueryVO;
import com.wwt.managemail.vo.BankTotalVo;

import java.util.List;

public interface BankService {
    Bank selectById(Integer id);
    /**
     * 保存银行卡信息
     * @param bank
     * @return
     */
    int insertSelective(Bank bank);

    Integer transaction(BankBill bankBill);

    List<Bank> selectAll();

    List<Bank> selectByQuery(BankQueryVO bankQueryVO);

    BankTotalVo selectTotal();

}
