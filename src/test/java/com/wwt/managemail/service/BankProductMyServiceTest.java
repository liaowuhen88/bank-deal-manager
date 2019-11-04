package com.wwt.managemail.service;

import com.alibaba.fastjson.JSON;
import com.wwt.managemail.ManagemailApplicationTests;
import com.wwt.managemail.entity.BankMyProduct;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

public class BankProductMyServiceTest extends ManagemailApplicationTests {
    @Autowired
    BankMyProductService bankMyProductService;

    @Test
    public void insert() {
        BankMyProduct vo = new BankMyProduct();
        vo.setBankCardId(2);
        vo.setBankProductId(1);
        vo.setBuyingTime(new Date());
        vo.setInvestmentAmount(10000L);
        vo.setInterestRate(12L);
        vo.setProfitDate(new Date());
        //vo.setTotalEffectiveUnterestIncome(10900L);
        vo.setDueTime(new Date());
        vo.setCreator("李杰");
        vo.setCreateTime(new Date());
        vo.setRemark("re,mark");
        logger.info(JSON.toJSONString(vo));
        int list = bankMyProductService.insert(vo);
        logger.info("*********************" + list);

    }
}
