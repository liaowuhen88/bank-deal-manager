package com.wwt.managemail.service.impl;

import com.wwt.managemail.entity.BankBill;
import com.wwt.managemail.enums.TransactionTypeEnum;
import com.wwt.managemail.mapper.BankBillMapper;
import com.wwt.managemail.service.BankBillService;
import com.wwt.managemail.service.BankService;
import com.wwt.managemail.vo.BankBillQuery;
import com.wwt.managemail.vo.BankBillVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class BankBillServiceImpl implements BankBillService {
    @Autowired
    private BankBillMapper bankBillMapper;

    @Autowired
    private BankService bankService;

    @Override
    public int insert(BankBill bankBill) {
        bankBill.setCreator("admin");
        bankBill.setCreateTime(new Date());
        return bankBillMapper.insert(bankBill);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int transaction(BankBill bankBill) {
        insert(bankBill);
        bankService.transaction(bankBill);
        //如果是转账,需要给转帐方增加记录
        if (TransactionTypeEnum.transfer_out.getCode() == bankBill.getTransactionType()) {
            BankBill bill = new BankBill();
            BeanUtils.copyProperties(bankBill, bill);
            bill.setTransactionType(TransactionTypeEnum.transfer_in.getCode());
            Integer bankCardId = bankBill.getBankCardId();
            bill.setBankCardId(bankBill.getTransferCard());
            bill.setId(null);
            bill.setTransferCard(bankCardId);
            insert(bill);
            bankService.transaction(bill);
        }
        return 0;
    }

    @Override
    public List<BankBillVo> query(BankBillQuery bankBillQuery) {
        BankBill query = new BankBill();
        query.setBankCardId(bankBillQuery.getBankCardId());
        return bankBillMapper.query();
    }
}
