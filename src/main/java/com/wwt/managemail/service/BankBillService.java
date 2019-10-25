package com.wwt.managemail.service;

import com.wwt.managemail.entity.Bank;
import com.wwt.managemail.entity.BankBill;
import com.wwt.managemail.vo.BankBillQuery;
import com.wwt.managemail.vo.BankInCome;

import java.util.List;

public interface BankBillService {
    int insert(BankBill bank);

    int insert(Bank bank, BankInCome bankInCome);

    List<BankBill> query(BankBillQuery bankBillQuery);
}
