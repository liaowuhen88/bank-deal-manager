package com.wwt.managemail.service;

import com.github.pagehelper.Page;
import com.wwt.managemail.entity.BankBill;
import com.wwt.managemail.vo.BankBillQuery;
import com.wwt.managemail.vo.BankBillTotalVo;
import com.wwt.managemail.vo.BankBillVo;
import com.wwt.managemail.vo.StackedLineChart;

import java.util.List;
import java.util.Map;

public interface BankBillService {
    int insertSelective(BankBill bank);

    int transaction(BankBill bank);

    Page<BankBillVo> query(BankBillQuery bankBillQuery);

    BankBillVo queryLaste(BankBillQuery bankBillQuery);

    List<BankBillTotalVo> queryCurrentYearTotal();

    List<BankBillTotalVo> totalByMonth(BankBillQuery bankBillQuery);

    Map<Integer, Map<String, BankBillTotalVo>> getBankBillTotalVoMap(List<BankBillTotalVo> list);

    StackedLineChart totalByMonthEchart(BankBillQuery bankBillQuery) throws Exception;
}
