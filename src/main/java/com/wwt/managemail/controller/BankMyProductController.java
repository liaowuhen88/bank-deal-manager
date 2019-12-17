package com.wwt.managemail.controller;

import com.wwt.managemail.common.Result;
import com.wwt.managemail.entity.BankMyProduct;
import com.wwt.managemail.enums.TransactionTypeEnum;
import com.wwt.managemail.service.BankMyProductService;
import com.wwt.managemail.service.BankProductService;
import com.wwt.managemail.service.BankService;
import com.wwt.managemail.vo.BankMyProductQueryVO;
import com.wwt.managemail.vo.BankMyProductVo;
import com.wwt.managemail.vo.ProductTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
     * @param productTransaction
     * @return
     */
    @PostMapping("income")
    public Result<Integer> income(@RequestBody ProductTransaction productTransaction) {
        int code = 0;
        if (TransactionTypeEnum.investment_redeem.getCode() == productTransaction.getTransactionType()) {
            code = bankMyProductService.redeem(productTransaction);
        } else {
            code = bankMyProductService.income(productTransaction);
        }
        return Result.sucess(code);
    }


    @PostMapping("select")
    public Result<List<BankMyProductVo>> select(@RequestBody BankMyProductQueryVO bankMyProductQueryVO) {
        List<BankMyProductVo> list = bankMyProductService.select(bankMyProductQueryVO);

        return Result.sucess(list);
    }

    @PostMapping("selectInterestPaymentMethod")
    public Result<List<String>> selectInterestPaymentMethod() {
        List<String> list = bankMyProductService.selectInterestPaymentMethod();

        return Result.sucess(list);
    }
}
