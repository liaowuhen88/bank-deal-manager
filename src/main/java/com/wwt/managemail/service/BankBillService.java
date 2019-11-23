package com.wwt.managemail.service;

import com.wwt.managemail.entity.BankBill;
import com.wwt.managemail.vo.BankBillQuery;
import com.wwt.managemail.vo.BankBillTotalVo;
import com.wwt.managemail.vo.BankBillVo;

import java.util.List;

public interface BankBillService {
    int insertSelective(BankBill bank);

    int transaction(BankBill bank);

    List<BankBillVo> query(BankBillQuery bankBillQuery);

    List<BankBillTotalVo> queryCurrentYearTotal();
}
