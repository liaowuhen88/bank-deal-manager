package com.wwt.managemail.service.impl;

import com.wwt.managemail.entity.Bank;
import com.wwt.managemail.mapper.BankMapper;
import com.wwt.managemail.mapper.MailMapper;
import com.wwt.managemail.service.BankBillService;
import com.wwt.managemail.service.BankService;
import com.wwt.managemail.vo.BankInCome;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class BankServiceImpl implements BankService {
    @Autowired
    private BankMapper bankMapper;

    @Autowired
    private BankBillService bankBillService;
    @Override
    public int insert(Bank bank) {
        bank.setCreator("admin");
        bank.setCreateTime(new Date());
        return bankMapper.insert(bank);
    }

    @Override
    public int income(BankInCome bankInCome) {
        Bank bank = bankMapper.selectByPrimaryKey(bankInCome.getBankCardId());
        bank.setCashAmount(bank.getCashAmount()+bankInCome.getAmount());
        bankMapper.updateByPrimaryKey(bank);
        bankBillService.insert(bank,bankInCome);
        return 0;
    }


    @Override
    public List<Bank> selectAll() {
        return  bankMapper.selectAll();
    }
}
