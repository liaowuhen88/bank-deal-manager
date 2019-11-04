package com.wwt.managemail.service.impl;

import com.wwt.managemail.entity.BankProduct;
import com.wwt.managemail.mapper.BankProductMapper;
import com.wwt.managemail.service.BankProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BankProductServiceImpl implements BankProductService {
    @Autowired
    private BankProductMapper bankProductMapper;

    @Override
    public int insert(BankProduct bankProduct) {
        return bankProductMapper.insert(bankProduct);
    }

    @Override
    public List<BankProduct> selectAll() {
        return bankProductMapper.selectAll();
    }
}
