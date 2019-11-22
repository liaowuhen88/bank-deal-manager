package com.wwt.managemail.controller;

import com.wwt.managemail.common.Result;
import com.wwt.managemail.service.BankService;
import com.wwt.managemail.vo.PageHomeVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/pageHome")
public class PageHomeController {
    @Autowired
    private BankService bankService;

    @PostMapping("/getTotal")
    public Result getTotal() {
        PageHomeVo pageHomeVo = bankService.selectTotal();
        return Result.sucess(pageHomeVo);
    }
}
