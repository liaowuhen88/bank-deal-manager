package com.wwt.managemail.controller;

import com.wwt.managemail.common.Result;
import com.wwt.managemail.entity.BankProduct;
import com.wwt.managemail.service.BankProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/bankProducts")
public class BankProductController extends BaseController {

    @Autowired
    BankProductService bankProductService;


    @PostMapping("insert")
    public Result<Integer> insert(@RequestBody BankProduct bankProduct) {
        int code = bankProductService.insert(bankProduct);
        return Result.sucess(code);
    }

    @PostMapping("selectAll")
    public Result<List<BankProduct>> selectAll() {
        List<BankProduct> list = bankProductService.selectAll();
        return Result.sucess(list);
    }
}
