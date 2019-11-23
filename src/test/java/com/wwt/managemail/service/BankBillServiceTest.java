package com.wwt.managemail.service;

import com.alibaba.fastjson.JSON;
import com.wwt.managemail.ManagemailApplicationTests;
import com.wwt.managemail.vo.BankBillTotalVo;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class BankBillServiceTest extends ManagemailApplicationTests {
    @Autowired
    BankBillService bankBillService;

    @Test
    public void queryCurrentYearTotal() {
        List<BankBillTotalVo> list = bankBillService.queryCurrentYearTotal();
        logger.info("*********************" + JSON.toJSONString(list));

    }
}
