package com.wwt.managemail.service.impl;

import com.wwt.managemail.entity.Bank;
import com.wwt.managemail.entity.BankBill;
import com.wwt.managemail.mapper.BankBillMapper;
import com.wwt.managemail.mapper.BankMapper;
import com.wwt.managemail.service.BankBillService;
import com.wwt.managemail.service.BankService;
import com.wwt.managemail.vo.BankBillQuery;
import com.wwt.managemail.vo.BankInCome;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class BankBillServiceImpl implements BankBillService {
    @Autowired
    private BankBillMapper bankBillMapper;
    @Override
    public int insert(BankBill bankBill) {
        bankBill.setCreator("admin");
        bankBill.setCreateTime(new Date());
        return bankBillMapper.insert(bankBill);
    }

    @Override
    public int insert(Bank bank, BankInCome bankInCome) {
        BankBill bankBill = new BankBill();
        bankBill.setBankCardId(bank.getId().longValue());
        bankBill.setTransactionAmount(bankInCome.getAmount());
        bankBill.setTransactionType("转入");
        return insert(bankBill);
    }

    @Override
    public List<BankBill> query(BankBillQuery bankBillQuery) {
        BankBill query = new BankBill();
        query.setBankCardId(bankBillQuery.getBankCardId());
        return bankBillMapper.selectByExample(query);
    }
}
