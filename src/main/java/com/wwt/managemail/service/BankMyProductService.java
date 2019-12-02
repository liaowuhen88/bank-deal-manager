package com.wwt.managemail.service;

import com.wwt.managemail.entity.BankMyProduct;
import com.wwt.managemail.vo.BankMyProductQueryVO;
import com.wwt.managemail.vo.BankMyProductVo;
import com.wwt.managemail.vo.ProductTransaction;

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

    /**
     * 赎回
     *
     * @param productTransaction
     * @return
     */
    int redeem(ProductTransaction productTransaction);

    int income(ProductTransaction productTransaction);

    List<BankMyProduct> selectAll();

    List<BankMyProductVo> select(BankMyProductQueryVO bankMyProductQueryVO);

    List<BankMyProductVo> expireProduct();

    /**
     * 到期利息
     *
     * @return
     */
    List<BankMyProductVo> expireInterest();
}
