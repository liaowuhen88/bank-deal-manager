package com.wwt.managemail.service;

import com.wwt.managemail.ManagemailApplicationTests;
import com.wwt.managemail.entity.Bank;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class BankServiceTest extends ManagemailApplicationTests {
    @Autowired
    BankService bankService;

    @Test
    public void insert() {
        Bank vo = new Bank();
        vo.setName("name");
        vo.setBankName("bankName");
        vo.setBankCard("bankCard");
        int list = bankService.insertSelective(vo);
        logger.info("*********************" + list);

    }
}
