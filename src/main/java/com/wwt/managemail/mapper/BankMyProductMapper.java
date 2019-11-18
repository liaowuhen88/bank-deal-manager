package com.wwt.managemail.mapper;

import com.wwt.managemail.entity.BankMyProduct;
import com.wwt.managemail.utils.CommonMapper;

public interface BankMyProductMapper extends CommonMapper<BankMyProduct> {
    int income(BankMyProduct bankMyProduct);
}
