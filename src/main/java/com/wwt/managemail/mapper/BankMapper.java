package com.wwt.managemail.mapper;

import com.wwt.managemail.entity.Bank;
import com.wwt.managemail.utils.CommonMapper;
import com.wwt.managemail.vo.PageHomeVo;

public interface BankMapper extends CommonMapper<Bank> {
    int transaction(Bank bank );

    PageHomeVo selectTotal();
}
