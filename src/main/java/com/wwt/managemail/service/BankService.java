package com.wwt.managemail.service;

import com.wwt.managemail.entity.Bank;

import java.util.List;

public interface BankService {
    int insert(Bank bank);

    List<Bank> selectAll();
}
