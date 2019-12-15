package com.wwt.managemail.service;

import com.github.pagehelper.Page;
import com.wwt.managemail.entity.BankBill;
import com.wwt.managemail.vo.BankBillQuery;
import com.wwt.managemail.vo.BankBillTotalVo;
import com.wwt.managemail.vo.BankBillVo;
import com.wwt.managemail.vo.StackedLineChart;

import java.util.List;

public interface BankBillService {
    int insertSelective(BankBill bank);

    int transaction(BankBill bank);

    Page<BankBillVo> query(BankBillQuery bankBillQuery);

    BankBillVo queryLaste(BankBillQuery bankBillQuery);

    List<BankBillTotalVo> queryCurrentYearTotal();

    StackedLineChart totalByMonth(BankBillQuery bankBillQuery) throws Exception;
}
