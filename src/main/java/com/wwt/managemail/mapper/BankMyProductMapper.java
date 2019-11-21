package com.wwt.managemail.mapper;

import com.wwt.managemail.entity.BankMyProduct;
import com.wwt.managemail.utils.CommonMapper;
import com.wwt.managemail.vo.BankMyProductQueryVO;

import java.util.List;

public interface BankMyProductMapper extends CommonMapper<BankMyProduct> {
    int income(BankMyProduct bankMyProduct);

    List<BankMyProduct> selectByBankMyProductQueryVO(BankMyProductQueryVO bankMyProductQueryVO);
}
