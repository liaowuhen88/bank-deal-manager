package com.wwt.managemail.controller;

import com.wwt.managemail.common.Result;
import com.wwt.managemail.entity.BankMyProduct;
import com.wwt.managemail.enums.TransactionTypeEnum;
import com.wwt.managemail.service.BankMyProductService;
import com.wwt.managemail.service.BankProductService;
import com.wwt.managemail.service.BankService;
import com.wwt.managemail.utils.BeanUtil;
import com.wwt.managemail.utils.TimeUtils;
import com.wwt.managemail.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
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

    @PostMapping("update")
    public Result<Integer> update(@RequestBody BankMyProductUpdateVO bankMyProductUpdateVO) {
        BankMyProduct bankMyProduct = BeanUtil.copyProperties(bankMyProductUpdateVO, BankMyProduct.class);
        int code = bankMyProductService.update(bankMyProduct);
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

    @PostMapping("selectByPrimaryKey")
    public Result<BankMyProductVo> selectByPrimaryKey(@RequestBody IdVo idVo) {
        BankMyProduct list = bankMyProductService.selectByPrimaryKey(idVo.getId());

        return Result.sucess(list);
    }



    @PostMapping("selectInterestPaymentMethod")
    public Result<List<String>> selectInterestPaymentMethod() {
        List<String> list = bankMyProductService.selectInterestPaymentMethod();

        return Result.sucess(list);
    }

    @PostMapping("getExpectedIncomePlan")
    public Result<List<ExpectedIncomePlanVo>> getExpectedIncomePlan(@Validated @RequestBody ExpectedIncomeTotalTableVo expectedIncomeTotalTableVo) throws Exception {
        if (null == expectedIncomeTotalTableVo.getStartTime() && null == expectedIncomeTotalTableVo.getEndTime()) {
            expectedIncomeTotalTableVo.setStartTime(TimeUtils.getCurrentYeadFirstDay());
            expectedIncomeTotalTableVo.setEndTime(new Date());
        }
        List<ExpectedIncomePlanVo> list = bankMyProductService.getExpectedIncomePlan(expectedIncomeTotalTableVo);

        return Result.sucess(list);
    }

    @PostMapping("expectedIncome")
    public Result<StackedLineChart> expectedIncome(@Validated @RequestBody ExpectedIncomeTotalTableVo expectedIncomeTotalTableVo) throws Exception {
        if (null == expectedIncomeTotalTableVo.getStartTime() && null == expectedIncomeTotalTableVo.getEndTime()) {
            expectedIncomeTotalTableVo.setStartTime(TimeUtils.getCurrentYeadFirstDay());
            expectedIncomeTotalTableVo.setEndTime(new Date());
        }
        StackedLineChart list = bankMyProductService.expectedIncome(expectedIncomeTotalTableVo);

        return Result.sucess(list);
    }

    @PostMapping("expectedIncomeTable")
    public Result<List<List<String>>> expectedIncomeTable(@Validated @RequestBody ExpectedIncomeTotalTableVo expectedIncomeTotalTableVo) throws Exception {
        if (null == expectedIncomeTotalTableVo.getStartTime() && null == expectedIncomeTotalTableVo.getEndTime()) {
            expectedIncomeTotalTableVo.setStartTime(TimeUtils.getCurrentYeadFirstDay());
            expectedIncomeTotalTableVo.setEndTime(new Date());
        }
        List<List<String>> list = bankMyProductService.expectedIncomeTable(expectedIncomeTotalTableVo);

        return Result.sucess(list);
    }

    @PostMapping("expectedIncomeTotal")
    public Result<StackedLineChart> expectedIncomeTotal(@Validated @RequestBody ExpectedIncomeTotalTableVo expectedIncomeTotalTableVo) throws Exception {
        if (null == expectedIncomeTotalTableVo.getStartTime() && null == expectedIncomeTotalTableVo.getEndTime()) {
            expectedIncomeTotalTableVo.setStartTime(TimeUtils.getCurrentYeadFirstDay());
            expectedIncomeTotalTableVo.setEndTime(new Date());
        }
        StackedLineChart list = bankMyProductService.expectedIncomeTotal(expectedIncomeTotalTableVo);

        return Result.sucess(list);
    }

    @PostMapping("expectedIncomeTotalTable")
    public Result<List<List<String>>> expectedIncomeTotalTable(@Validated @RequestBody ExpectedIncomeTotalTableVo expectedIncomeTotalTableVo) throws Exception {
        if (null == expectedIncomeTotalTableVo.getStartTime() && null == expectedIncomeTotalTableVo.getEndTime()) {
            expectedIncomeTotalTableVo.setStartTime(TimeUtils.getCurrentYeadFirstDay());
            expectedIncomeTotalTableVo.setEndTime(new Date());
        }
        List<List<String>> list = bankMyProductService.expectedIncomeTotalTable(expectedIncomeTotalTableVo);
        return Result.sucess(list);
    }
}
