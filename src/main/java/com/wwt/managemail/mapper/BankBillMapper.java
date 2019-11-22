package com.wwt.managemail.mapper;

import com.wwt.managemail.entity.BankBill;
import com.wwt.managemail.utils.CommonMapper;
import com.wwt.managemail.vo.BankBillQuery;
import com.wwt.managemail.vo.BankBillVo;

import java.util.List;

public interface BankBillMapper extends CommonMapper<BankBill> {

   List<BankBillVo> query(BankBillQuery bankBillQuery);
}
