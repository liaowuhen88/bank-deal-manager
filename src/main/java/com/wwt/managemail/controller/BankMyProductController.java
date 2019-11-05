package com.wwt.managemail.controller;

import com.wwt.managemail.common.Result;
import com.wwt.managemail.entity.Bank;
import com.wwt.managemail.entity.BankMyProduct;
import com.wwt.managemail.entity.BankProduct;
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

    @PostMapping("insert")
    public Result<Integer> insert(@RequestBody BankMyProduct bankMyProduct) {
        int code = bankMyProductService.insert(bankMyProduct);
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

        // 产品
        List<BankProduct> bankProducts = bankProductService.selectAll();
        // 主键关系
        Map<Integer, BankProduct> bankProductIds = bankProducts.stream()
                .collect(Collectors.toMap(BankProduct::getId,
                        paramVO -> paramVO));
        for (BankMyProduct bankMyProduct : list) {
            BankMyProductVo vo = new BankMyProductVo();
            BeanUtils.copyProperties(bankMyProduct, vo);
            vo.setBank(bankIds.get(bankMyProduct.getBankCardId()));
            vo.setBankProduct(bankProductIds.get(bankMyProduct.getBankProductId()));
            res.add(vo);
        }
        return Result.sucess(res);
    }
}
