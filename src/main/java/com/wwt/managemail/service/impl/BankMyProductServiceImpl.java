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
import com.wwt.managemail.vo.BankMyProductVo;
import com.wwt.managemail.vo.ProductTransaction;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        bankMyProduct.setState(1);
        int code = insertSelective(bankMyProduct);

        BankBill bankBill = new BankBill();
        bankBill.setBankCardId(bankMyProduct.getBankCardId());
        bankBill.setTransactionAmount(bankMyProduct.getInvestmentAmount());
        bankBill.setMyProductId(bankMyProduct.getId());
        bankBill.setTransactionType(TransactionTypeEnum.investment.getCode());
        bankBill.setTransactionTime(bankMyProduct.getBuyingTime());
        bankBill.setRemark(bankMyProduct.getRemark());
        bankService.transaction(bankBill);
        bankBillService.insertSelective(bankBill);
        return code;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int redeem(ProductTransaction bankBill) {
        // 基金赎回，增加利息，修改状态
        BankMyProduct bankMyProduct = new BankMyProduct();
        bankMyProduct.setId(bankBill.getMyProductId());
        bankMyProduct.setTotalEffectiveInterestIncome(bankBill.getTransactionAmount());
        bankMyProduct.setProfitDate(bankBill.getNextProfitDate());
        bankMyProduct.setState(2);
        bankMyProductMapper.transaction(bankMyProduct);
        //账单，先记录利息
        bankBill.setTransactionType(TransactionTypeEnum.investmentIncome.getCode());
        bankBillService.insertSelective(bankBill);
        bankService.transaction(bankBill);
        // 在记录本金
        BankMyProduct bankMyProductDb = bankMyProductMapper.selectByPrimaryKey(bankBill.getMyProductId());
        bankBill.setTransactionAmount(bankMyProductDb.getInvestmentAmount());
        bankBill.setTransactionType(TransactionTypeEnum.investment_redeem_principal.getCode());
        bankBill.setId(null);
        bankBillService.insertSelective(bankBill);
        //银行卡做账
        bankBill.setTransactionAmount(bankMyProductDb.getInvestmentAmount());
        bankService.transaction(bankBill);
        return 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int income(ProductTransaction bankBill) {
        BankMyProduct bankMyProduct = new BankMyProduct();
        bankMyProduct.setId(bankBill.getMyProductId());
        bankMyProduct.setTotalEffectiveInterestIncome(bankBill.getTransactionAmount());
        bankMyProduct.setProfitDate(bankBill.getNextProfitDate());
        bankMyProductMapper.transaction(bankMyProduct);
        bankService.transaction(bankBill);
        bankBillService.insertSelective(bankBill);
        return 0;
    }

    @Override
    public List<BankMyProduct> selectAll() {
        return bankMyProductMapper.selectAll();
    }

    @Override
    public List<BankMyProductVo> select(BankMyProductQueryVO bankMyProductQueryVO) {
        List<BankMyProduct> list = bankMyProductMapper.selectByBankMyProductQueryVO(bankMyProductQueryVO);
        return getBankMyProductVos(list);
    }

    @Override
    public List<BankMyProductVo> expireProduct() {
        BankMyProductQueryVO bankMyProductQueryVO = new BankMyProductQueryVO();
        bankMyProductQueryVO.setExpireProductTime(new Date());
        List<BankMyProduct> list = bankMyProductMapper.selectByBankMyProductQueryVO(bankMyProductQueryVO);
        return getBankMyProductVos(list);
    }

    @Override
    public List<BankMyProductVo> expireInterest() {
        BankMyProductQueryVO bankMyProductQueryVO = new BankMyProductQueryVO();
        bankMyProductQueryVO.setExpireInterestTime(new Date());
        List<BankMyProduct> list = bankMyProductMapper.selectByBankMyProductQueryVO(bankMyProductQueryVO);
        return getBankMyProductVos(list);
    }

    List<BankMyProductVo> getBankMyProductVos(List<BankMyProduct> list) {
        if (null != list) {
            List<BankMyProductVo> res = new ArrayList<>(list.size());

            List<Bank> banks = bankService.selectAll();
            // 主键关系
            Map<Integer, Bank> bankIds = banks.stream()
                    .collect(Collectors.toMap(Bank::getId,
                            paramVO -> paramVO));

            for (BankMyProduct bankMyProduct : list) {
                BankMyProductVo vo = new BankMyProductVo();
                BeanUtils.copyProperties(bankMyProduct, vo);
                vo.setBank(bankIds.get(bankMyProduct.getBankCardId()));
                res.add(vo);
            }
            return res;
        }
        return null;
    }
}
