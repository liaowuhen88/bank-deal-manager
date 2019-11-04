package com.wwt.managemail.service.impl;

import com.wwt.managemail.entity.Bank;
import com.wwt.managemail.entity.BankBill;
import com.wwt.managemail.mapper.BankMapper;
import com.wwt.managemail.service.BankService;
import com.wwt.managemail.utils.MoneyUtills;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
public class BankServiceImpl implements BankService {
    @Autowired
    private BankMapper bankMapper;

    @Override
    public int insert(Bank bank) {
        bank.setCreator("admin");
        bank.setCreateTime(new Date());
        return bankMapper.insert(bank);
    }

    @Override
    public int transaction(BankBill bankBill) {
        Bank bank = new Bank();
        bank.setId(Math.toIntExact(bankBill.getBankCardId()));
        // 支出等需要取负数
        BigDecimal realMoney = MoneyUtills.getRealMoney(bankBill.getTransactionType(), bankBill.getTransactionAmount());
        bank.setCashAmount(realMoney);
        return bankMapper.transaction(bank);
    }


    @Override
    public List<Bank> selectAll() {
        return  bankMapper.selectAll();
    }
}