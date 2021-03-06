package com.wwt.managemail.controller;

import com.github.pagehelper.Page;
import com.wwt.managemail.common.Result;
import com.wwt.managemail.entity.Bank;
import com.wwt.managemail.mapper.BankNameMapper;
import com.wwt.managemail.service.BankService;
import com.wwt.managemail.vo.BankQueryVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("api/banks")
public class BankController extends BaseController {

    @Autowired
    BankService bankService;
    @Autowired
    BankNameMapper bankNameMapper;

    @PostMapping("getBankName")
    public Result<String> getBankName() {
        return Result.sucess(bankNameMapper.selectAll());
    }

    @PostMapping("insert")
    public Result<Integer> insert(@RequestBody Bank bank) {
        int code = bankService.insertSelective(bank);
        return Result.sucess(code);
    }

    @PostMapping("selectAll")
    public Result<List<Bank>> selectAll() {
        List<Bank> list = bankService.selectAll();
        return Result.sucess(list);
    }

    @PostMapping("select")
    public Result<Page<Bank>> select(@RequestBody BankQueryVO bankQueryVO) {
        List<Bank> list = bankService.selectByQuery(bankQueryVO);
        Result result = Result.sucess(list);
        return result;
    }

    @PostMapping("selectUserNamesAndBankNames")
    public Result<Map<String, Set<String>>> selectUserNamesAndBankNames() {
        Map<String, Set<String>> map = new HashMap();
        Set<String> userNames = new HashSet<>();
        Set<String> bankNames = new HashSet<>();
        Set<String> bankCards = new HashSet<>();
        map.put("userNames", userNames);
        map.put("bankNames", bankNames);
        map.put("bankCards", bankCards);
        List<Bank> list = bankService.selectAll();
        if (null != list && list.size() > 0) {
            for (Bank bank : list) {
                userNames.add(bank.getName());
                bankNames.add(bank.getBankName());
                bankCards.add(bank.getBankCard());
            }
        }
        return Result.sucess(map);
    }
}
