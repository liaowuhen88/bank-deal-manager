package com.wwt.managemail.service;

import com.wwt.managemail.ManagemailApplicationTests;
import com.wwt.managemail.entity.BankProduct;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

public class BankProductServiceTest extends ManagemailApplicationTests {
    @Autowired
    BankProductService bankProductService;

    @Test
    public void insert() {
        BankProduct vo = new BankProduct();
        vo.setBank("交通银行");
        vo.setBankProduct("大额存单");
        vo.setCreator("李杰");
        vo.setCreatorTime(new Date());
        vo.setDepositPeriod(100);
        vo.setProductType("大额存单");
        vo.setInterestPaymentMethod("日");
        int list = bankProductService.insert(vo);
        logger.info("*********************" + list);

    }
}
