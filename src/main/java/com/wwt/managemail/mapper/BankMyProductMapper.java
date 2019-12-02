package com.wwt.managemail.mapper;

import com.wwt.managemail.entity.BankMyProduct;
import com.wwt.managemail.utils.CommonMapper;
import com.wwt.managemail.vo.BankMyProductQueryVO;

import java.util.List;

public interface BankMyProductMapper extends CommonMapper<BankMyProduct> {
    /**
     * 利息收入
     *
     * @param bankMyProduct
     * @return
     */
    int transaction(BankMyProduct bankMyProduct);


    List<BankMyProduct> selectByBankMyProductQueryVO(BankMyProductQueryVO bankMyProductQueryVO);
}
