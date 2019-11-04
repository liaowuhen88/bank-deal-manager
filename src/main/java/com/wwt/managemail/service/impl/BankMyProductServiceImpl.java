package com.wwt.managemail.service.impl;

import com.wwt.managemail.entity.BankMyProduct;
import com.wwt.managemail.mapper.BankMyProductMapper;
import com.wwt.managemail.service.BankMyProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BankMyProductServiceImpl implements BankMyProductService {
    @Autowired
    private BankMyProductMapper bankMyProductMapper;

    @Override
    public int insert(BankMyProduct bankMyProduct) {
        return bankMyProductMapper.insert(bankMyProduct);
    }

    @Override
    public List<BankMyProduct> selectAll() {
        return bankMyProductMapper.selectAll();
    }
}
