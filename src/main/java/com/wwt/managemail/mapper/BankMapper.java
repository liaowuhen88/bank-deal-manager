package com.wwt.managemail.mapper;

import com.wwt.managemail.entity.Bank;
import com.wwt.managemail.utils.CommonMapper;
import com.wwt.managemail.vo.BankTotalVo;

import java.util.List;

public interface BankMapper extends CommonMapper<Bank> {

    List<Bank> selectByQuery(Bank bank);

    Integer transaction(Bank bank);

    /**
     * 统计所有得现金和投资金额
     *
     * @return
     */
    BankTotalVo selectTotal();
}
