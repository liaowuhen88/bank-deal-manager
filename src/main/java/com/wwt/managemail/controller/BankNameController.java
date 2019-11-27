package com.wwt.managemail.controller;

import com.wwt.managemail.common.Result;
import com.wwt.managemail.entity.BankName;
import com.wwt.managemail.mapper.BankNameMapper;
import com.wwt.managemail.service.BankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/bankNames")
public class BankNameController extends BaseController {

    @Autowired
    BankService bankService;
    @Autowired
    BankNameMapper bankNameMapper;

    @PostMapping("getBankName")
    public Result<BankName> getBankName() {
        return Result.sucess(bankNameMapper.selectAll());
    }

    @PostMapping("addBankName")
    public Result<String> insert(@RequestBody BankName bankName) {
        return Result.sucess(bankNameMapper.insert(bankName));
    }
}
