package com.wwt.managemail.service;

import com.wwt.managemail.entity.BankMyProduct;

import java.util.List;

public interface BankMyProductService {
    /**
     * 保存理财数据
     *
     * @param bankMyProduct
     * @return
     */
    int insert(BankMyProduct bankMyProduct);

    List<BankMyProduct> selectAll();
}
