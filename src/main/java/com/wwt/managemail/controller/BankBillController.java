package com.wwt.managemail.controller;

import com.wwt.managemail.common.Result;
import com.wwt.managemail.entity.BankBill;
import com.wwt.managemail.service.BankBillService;
import com.wwt.managemail.vo.BankBillQuery;
import com.wwt.managemail.vo.BankBillVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/bankBill")
public class BankBillController {

    @Autowired
    BankBillService billService;


    @PostMapping("transaction")
    public Result<Integer> transaction(@Validated @RequestBody BankBill bankBill){
        int code = billService.transaction(bankBill);
        return Result.sucess(code);
    }

    @PostMapping("query")
    public Result< List<BankBillVo>> query(@Validated @RequestBody BankBillQuery bankBillQuery){
        List<BankBillVo> list = billService.query(bankBillQuery);
        return Result.sucess(list);
    }
}
