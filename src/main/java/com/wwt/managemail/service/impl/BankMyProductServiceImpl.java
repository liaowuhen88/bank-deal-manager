package com.wwt.managemail.service.impl;

import com.wwt.managemail.entity.Bank;
import com.wwt.managemail.entity.BankBill;
import com.wwt.managemail.entity.BankMyProduct;
import com.wwt.managemail.enums.TransactionTypeEnum;
import com.wwt.managemail.mapper.BankMyProductMapper;
import com.wwt.managemail.service.BankBillService;
import com.wwt.managemail.service.BankMyProductService;
import com.wwt.managemail.service.BankService;
import com.wwt.managemail.vo.BankMyProductQueryVO;
import com.wwt.managemail.vo.ProductIncome;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BankMyProductServiceImpl implements BankMyProductService {
    @Autowired
    private BankMyProductMapper bankMyProductMapper;
    @Autowired
    private BankService bankService;
    @Autowired
    private BankBillService bankBillService;

    @Override
    public int insertSelective(BankMyProduct bankMyProduct) {
        return bankMyProductMapper.insertSelective(bankMyProduct);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int buy(BankMyProduct bankMyProduct) {
        Bank bank = bankService.selectById(bankMyProduct.getBankCardId());
        if (bank.getCashAmount().compareTo(bankMyProduct.getInvestmentAmount()) == -1) {
            throw new RuntimeException("金额不足");
        }
        BankBill bankBill = new BankBill();
        bankBill.setBankCardId(bankMyProduct.getBankCardId());
        bankBill.setTransactionAmount(bankMyProduct.getInvestmentAmount());
        bankBill.setTransactionType(TransactionTypeEnum.investment.getCode());
        bankBill.setTransactionTime(bankMyProduct.getBuyingTime());
        bankBill.setRemark(bankMyProduct.getRemark());
        bankService.transaction(bankBill);
        bankBillService.insertSelective(bankBill);

        return insertSelective(bankMyProduct);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int income(ProductIncome bankBill) {
        BankMyProduct bankMyProduct = new BankMyProduct();
        bankMyProduct.setId(bankBill.getMyProductId());
        bankMyProduct.setTotalEffectiveInterestIncome(bankBill.getTransactionAmount());
        bankMyProduct.setProfitDate(bankBill.getNextProfitDate());
        bankMyProductMapper.income(bankMyProduct);
        bankService.transaction(bankBill);
        bankBillService.insertSelective(bankBill);
        return 0;
    }

    @Override
    public List<BankMyProduct> selectAll() {
        return bankMyProductMapper.selectAll();
    }

    @Override
    public List<BankMyProduct> select(BankMyProductQueryVO bankMyProductQueryVO) {
        return bankMyProductMapper.selectByBankMyProductQueryVO(bankMyProductQueryVO);
    }
}
