package com.wwt.managemail.controller;

import com.wwt.managemail.common.Result;
import com.wwt.managemail.entity.Bank;
import com.wwt.managemail.service.BankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("api/banks")
public class BankController extends BaseController{

    @Autowired
    BankService bankService;

    @PostMapping("getBankName")
    public Result<String> getBankName() {
        List<String> list = new ArrayList<>();
        list.add("招商银行");
        list.add("交通银行");
        list.add("建设银行");
        list.add("天津银行");
        list.add("工商银行");
        list.add("中信银行");
        return Result.sucess(list);
    }

    @PostMapping("insert")
    public Result<Integer> insert(@RequestBody Bank bank) {
        int code = bankService.insert(bank);
        return Result.sucess(code);
    }

    @PostMapping("selectAll")
    public Result<List<Bank>> selectAll(){
        List<Bank>  list = bankService.selectAll();
        return Result.sucess(list);
    }
}
