package com.wwt.managemail.controller;

import com.wwt.managemail.common.Result;
import com.wwt.managemail.entity.Bank;
import com.wwt.managemail.entity.BankBill;
import com.wwt.managemail.entity.BankMyProduct;
import com.wwt.managemail.service.BankMyProductService;
import com.wwt.managemail.service.BankProductService;
import com.wwt.managemail.service.BankService;
import com.wwt.managemail.vo.BankMyProductVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/bankMyProducts")
public class BankMyProductController extends BaseController {

    @Autowired
    BankMyProductService bankMyProductService;
    @Autowired
    BankService bankService;
    @Autowired
    BankProductService bankProductService;

    @PostMapping("buy")
    public Result<Integer> buy(@RequestBody BankMyProduct bankMyProduct) {
        int code = bankMyProductService.buy(bankMyProduct);
        return Result.sucess(code);
    }

    /**
     * 利息收入
     *
     * @param bankBill
     * @return
     */
    @PostMapping("income")
    public Result<Integer> income(@RequestBody BankBill bankBill) {
        int code = bankMyProductService.income(bankBill);
        return Result.sucess(code);
    }

    @PostMapping("selectAll")
    public Result<List<BankMyProductVo>> selectAll() {
        List<BankMyProduct> list = bankMyProductService.selectAll();
        List<BankMyProductVo> res = new ArrayList<>(list.size());

        List<Bank> banks = bankService.selectAll();
        // 主键关系
        Map<Integer, Bank> bankIds = banks.stream()
                .collect(Collectors.toMap(Bank::getId,
                        paramVO -> paramVO));

        for (BankMyProduct bankMyProduct : list) {
            BankMyProductVo vo = new BankMyProductVo();
            BeanUtils.copyProperties(bankMyProduct, vo);
            vo.setBank(bankIds.get(bankMyProduct.getBankCardId()));
            res.add(vo);
        }
        return Result.sucess(res);
    }
}