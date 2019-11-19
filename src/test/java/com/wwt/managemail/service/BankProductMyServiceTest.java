package com.wwt.managemail.service;

import com.alibaba.fastjson.JSON;
import com.wwt.managemail.ManagemailApplicationTests;
import com.wwt.managemail.entity.BankMyProduct;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Date;

public class BankProductMyServiceTest extends ManagemailApplicationTests {
    @Autowired
    BankMyProductService bankMyProductService;

    @Test
    public void insert() {
        BankMyProduct vo = new BankMyProduct();
        vo.setBankCardId(2);
        vo.setBuyingTime(new Date());
        vo.setInvestmentAmount(new BigDecimal(100.98));
        vo.setInterestRate(new BigDecimal(12));
        vo.setProfitDate(new Date());
        //vo.setTotalEffectiveUnterestIncome(10900L);
        vo.setDueTime(new Date());
        vo.setCreator("李杰");
        vo.setCreateTime(new Date());
        vo.setRemark("re,mark");
        logger.info(JSON.toJSONString(vo));
        int list = bankMyProductService.insertSelective(vo);
        logger.info("*********************" + list);

    }
}
