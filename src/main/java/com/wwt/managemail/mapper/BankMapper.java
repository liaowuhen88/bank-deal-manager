package com.wwt.managemail.mapper;

import com.wwt.managemail.entity.Bank;
import com.wwt.managemail.utils.CommonMapper;
import com.wwt.managemail.vo.BankTotalVo;

public interface BankMapper extends CommonMapper<Bank> {
    int transaction(Bank bank );

    /**
     * 统计所有得现金和投资金额
     *
     * @return
     */
    BankTotalVo selectTotal();
}
