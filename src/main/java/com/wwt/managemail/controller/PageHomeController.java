package com.wwt.managemail.controller;

import com.wwt.managemail.common.Result;
import com.wwt.managemail.service.BankBillService;
import com.wwt.managemail.service.BankMyProductService;
import com.wwt.managemail.service.BankService;
import com.wwt.managemail.vo.BankBillTotalVo;
import com.wwt.managemail.vo.BankMyProductVo;
import com.wwt.managemail.vo.BankTotalVo;
import com.wwt.managemail.vo.PageHomeVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/pageHome")
public class PageHomeController extends BaseController {
    @Autowired
    private BankService bankService;
    @Autowired
    private BankBillService bankBillService;
    @Autowired
    private BankMyProductService bankMyProductService;


    @PostMapping("/getTotal")
    public Result getTotal() {
        PageHomeVo pageHomeVo = new PageHomeVo();
        BankTotalVo bankTotalVo = bankService.selectTotal();
        List<BankBillTotalVo> list = bankBillService.queryCurrentYearTotal();
        Map<String, BankBillTotalVo> map = list.stream().collect(Collectors.toMap(BankBillTotalVo::getTransactionTypeString, account -> account));

        List<BankMyProductVo> expireProduct = bankMyProductService.expireProduct();
        List<BankMyProductVo> expireInterest = bankMyProductService.expireInterest();
        pageHomeVo.setIntegerBankBillTotalVoMap(map);
        pageHomeVo.setBankTotalVo(bankTotalVo);
        pageHomeVo.setExpireProduct(expireProduct);
        pageHomeVo.setExpireInterest(expireInterest);
        return Result.sucess(pageHomeVo);
    }
}
