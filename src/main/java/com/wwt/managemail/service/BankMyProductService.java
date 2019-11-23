package com.wwt.managemail.service;

import com.wwt.managemail.entity.BankMyProduct;
import com.wwt.managemail.vo.BankMyProductQueryVO;
import com.wwt.managemail.vo.ProductIncome;

import java.util.List;

public interface BankMyProductService {
    /**
     * 保存理财数据
     *
     * @param bankMyProduct
     * @return
     */
    int insertSelective(BankMyProduct bankMyProduct);

    int buy(BankMyProduct bankMyProduct);

    int income(ProductIncome productIncome);

    List<BankMyProduct> selectAll();

    List<BankMyProduct> select(BankMyProductQueryVO bankMyProductQueryVO);

    List<BankMyProduct> expireProduct();
}
