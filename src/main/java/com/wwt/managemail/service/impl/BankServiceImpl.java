package com.wwt.managemail.service.impl;

import com.wwt.managemail.entity.Bank;
import com.wwt.managemail.mapper.BankMapper;
import com.wwt.managemail.service.BankBillService;
import com.wwt.managemail.service.BankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public List<Bank> selectAll() {
        return  bankMapper.selectAll();
    }
}
