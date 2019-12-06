package com.wwt.managemail.service.impl;

import com.wwt.managemail.entity.Bank;
import com.wwt.managemail.entity.BankBill;
import com.wwt.managemail.enums.TransactionTypeEnum;
import com.wwt.managemail.mapper.BankMapper;
import com.wwt.managemail.service.BankBillService;
import com.wwt.managemail.service.BankService;
import com.wwt.managemail.utils.MoneyUtills;
import com.wwt.managemail.vo.BankQueryVO;
import com.wwt.managemail.vo.BankTotalVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
public class BankServiceImpl implements BankService {
    @Autowired
    private BankMapper bankMapper;
    @Autowired
    private BankBillService bankBillService;


    @Override
    public Bank selectById(Integer id) {
        return bankMapper.selectByPrimaryKey(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertSelective(Bank bank) {

        bank.setCreator("admin");
        bank.setCreateTime(new Date());
        int count = bankMapper.insertSelective(bank);

        BankBill bill = new BankBill();
        bill.setTransactionType(TransactionTypeEnum.init_new.getCode());
        bill.setBankCardId(bank.getId());
        bill.setTransactionAmount(bank.getCashAmount());
        bill.setTransactionTime(new Date());

        bankBillService.insertSelective(bill);

        return count;
    }

    @Override
    public Integer transaction(BankBill bankBill) {
        BigDecimal realMoney = MoneyUtills.getRealMoney(bankBill.getTransactionType(), bankBill.getTransactionAmount());
        Bank bankDb = selectById(bankBill.getBankCardId());
        if (bankDb.getCashAmount().add(realMoney).compareTo(new BigDecimal("0")) == -1) {
            throw new RuntimeException("金额不足");
        }
        Bank bank = new Bank();
        bank.setId(Math.toIntExact(bankBill.getBankCardId()));
        // 支出等需要取负数

        bank.setCashAmount(realMoney);
        if (TransactionTypeEnum.investment.getCode() == bankBill.getTransactionType()) {
            bank.setInvestmentAmount(bankBill.getTransactionAmount());
        }
        if (TransactionTypeEnum.investment_redeem_principal.getCode() == bankBill.getTransactionType()) {
            bank.setInvestmentAmount(bankBill.getTransactionAmount().negate());
        }

        return bankMapper.transaction(bank);
    }


    @Override
    public List<Bank> selectAll() {
        return  bankMapper.selectAll();
    }

    @Override
    public List<Bank> select(BankQueryVO bankQueryVO) {
        Bank bank = new Bank();
        if (StringUtils.isNotEmpty(bankQueryVO.getUserName())) {
            bank.setName(bankQueryVO.getUserName());
        }
        if (StringUtils.isNotEmpty(bankQueryVO.getBankName())) {
            bank.setBankName(bankQueryVO.getBankName());
        }
        return bankMapper.select(bank);
    }

    @Override
    public BankTotalVo selectTotal() {
        return bankMapper.selectTotal();
    }
}
