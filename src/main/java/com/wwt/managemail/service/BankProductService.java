package com.wwt.managemail.service;

import com.wwt.managemail.entity.BankProduct;

import java.util.List;

public interface BankProductService {
    /**
     * 保存理财数据
     *
     * @param bankProduct
     * @return
     */
    int insert(BankProduct bankProduct);

    List<BankProduct> selectAll();
}
