package com.wwt.managemail.controller;

import com.wwt.managemail.common.PageResult;
import com.wwt.managemail.common.Result;
import com.wwt.managemail.dto.MailDTO;
import com.wwt.managemail.entity.Bank;
import com.wwt.managemail.service.BankService;
import com.wwt.managemail.service.MailService;
import com.wwt.managemail.vo.BankInCome;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/banks")
public class BankController extends BaseController{

    @Autowired
    BankService bankService;

    @PostMapping("insert")
    public Result<Integer> insert(@RequestBody Bank bank){
        int code = bankService.insert(bank);
        return Result.sucess(code);
    }

    @PostMapping("income")
    public Result<Integer> income(@Validated @RequestBody BankInCome bankInCome){
        int code = bankService.income(bankInCome);
        return Result.sucess(code);
    }

    @PostMapping("selectAll")
    public Result<List<Bank>> selectAll(){
        List<Bank>  list = bankService.selectAll();
        return Result.sucess(list);
    }
}
