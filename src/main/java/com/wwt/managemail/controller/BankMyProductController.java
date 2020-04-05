package com.wwt.managemail.controller;

import com.wwt.managemail.common.Result;
import com.wwt.managemail.entity.Bank;
import com.wwt.managemail.entity.BankMyProduct;
import com.wwt.managemail.enums.TransactionTypeEnum;
import com.wwt.managemail.service.BankBillService;
import com.wwt.managemail.service.BankMyProductService;
import com.wwt.managemail.service.BankProductService;
import com.wwt.managemail.service.BankService;
import com.wwt.managemail.utils.BeanUtil;
import com.wwt.managemail.utils.BigDecimalUtils;
import com.wwt.managemail.utils.TimeUtils;
import com.wwt.managemail.vo.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.*;
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
    @Autowired
    BankBillService billService;

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


    @PostMapping("delete")
    public Result<Integer> delete(@Validated @RequestBody IdVo idVo) {
        BankMyProduct bankMyProduct = new BankMyProduct();
        bankMyProduct.setId(idVo.getId());
        bankMyProduct.setState(3);
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
        BankMyProduct bankMyProduct = bankMyProductService.selectByPrimaryKey(idVo.getId());
        BankMyProductVo vo = new BankMyProductVo();
        BeanUtils.copyProperties(bankMyProduct, vo);
        Bank bank = bankService.selectById(bankMyProduct.getBankCardId());
        vo.setBank(bank);
        return Result.sucess(vo);
    }


    @PostMapping("selectInterestPaymentMethod")
    public Result<List<String>> selectInterestPaymentMethod() {
        List<String> list = bankMyProductService.selectInterestPaymentMethod();

        return Result.sucess(list);
    }

    @PostMapping("getExpectedIncomePlan")
    public Result<List<ExpectedIncomePlanVo>> getExpectedIncomePlan(@Validated @RequestBody ExpectedIncomeTotalTableVo expectedIncomeTotalTableVo) throws Exception {
        init(expectedIncomeTotalTableVo);
        List<ExpectedIncomePlanVo> list = bankMyProductService.getExpectedIncomePlan(expectedIncomeTotalTableVo);

        return Result.sucess(list);
    }

    @PostMapping("getExpectedIncomePlanAndReal")
    public Result<List<ExpectedIncomePlanVo>> getExpectedIncomePlanAndReal(@Validated @RequestBody ExpectedIncomeTotalTableVo expectedIncomeTotalTableVo) throws Exception {
        //1.预期利息收入
        init(expectedIncomeTotalTableVo);
        // 获取产品数据
        BankMyProductQueryVO bankMyProductQueryVO = new BankMyProductQueryVO();
        bankMyProductQueryVO.setState(new Integer[]{1, 2});
        List<BankMyProductVo> vos = bankMyProductService.select(bankMyProductQueryVO);
        // 生成预期利息计划表
        List<ExpectedIncomePlanVo> list = bankMyProductService.getExpectedIncomePlan(expectedIncomeTotalTableVo, vos);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (BankMyProductVo vo : vos) {
            if (expectedIncomeTotalTableVo.getStartTime().getTime() <= vo.getDueTime().getTime() && expectedIncomeTotalTableVo.getEndTime().getTime() >= vo.getDueTime().getTime()) {
                ExpectedIncomePlanVo planVo = new ExpectedIncomePlanVo();
                planVo.setId(Integer.valueOf(vo.getId()));
                planVo.setTime(sdf.format(vo.getDueTime()));
                planVo.setExpectedInvestmentAmount(vo.getInvestmentAmount());
                list.add(planVo);
            }
        }
        // 查询已删除订单
        bankMyProductQueryVO = new BankMyProductQueryVO();
        bankMyProductQueryVO.setState(new Integer[]{3});
        List<BankMyProductVo> bankMyProductVos = bankMyProductService.select(bankMyProductQueryVO);
        Map<Integer, BankMyProductVo> map = bankMyProductVos.stream().collect(
                Collectors.toMap(BankMyProductVo::getId, p -> p));

        //查询交易流水
        QueryByTimeVo queryByTimeVo = new QueryByTimeVo();
        queryByTimeVo.setTime(expectedIncomeTotalTableVo.getTime());
        queryByTimeVo.setTransactionTypes(new int[]{6, 11});
        List<BankBillVo> bankBillVos = billService.queryNoPage(queryByTimeVo);
        Map<Integer, ExpectedIncomePlanVo> mapBill = new HashMap<>();
        if (null != bankBillVos) {

            for (BankBillVo vo : bankBillVos) {
                Integer myProductId = Integer.valueOf(vo.getMyProductId());
                // 如果是删除订单，则不统计
                if (null != map.get(Integer.valueOf(vo.getMyProductId()))) {
                    logger.info("******{}", vo.getMyProductId());
                    continue;
                }
                ExpectedIncomePlanVo planVo = mapBill.get(myProductId);
                if (null == planVo) {
                    planVo = new ExpectedIncomePlanVo();
                    planVo.setId(myProductId);
                    planVo.setTime(sdf.format(vo.getTransactionTime()));
                    mapBill.put(myProductId, planVo);
                }
                if (vo.getTransactionType() == 6) {
                    planVo.setRealInterestIncome(BigDecimalUtils.add(vo.getTransactionAmount(), planVo.getRealInterestIncome()));
                } else if (vo.getTransactionType() == 11) {
                    planVo.setInvestmentAmount(BigDecimalUtils.add(vo.getTransactionAmount(), planVo.getInvestmentAmount()));
                }
            }
            list.addAll(mapBill.values());
        }
        list.sort(Comparator.comparing(ExpectedIncomePlanVo::getId));
        return Result.sucess(list);
    }

    @PostMapping("expectedIncome")
    public Result<StackedLineChart> expectedIncome(@Validated @RequestBody ExpectedIncomeTotalTableVo expectedIncomeTotalTableVo) throws Exception {
        init(expectedIncomeTotalTableVo);
        StackedLineChart list = bankMyProductService.expectedIncome(expectedIncomeTotalTableVo);

        return Result.sucess(list);
    }

    @PostMapping("expectedIncomeTable")
    public Result<List<List<String>>> expectedIncomeTable(@Validated @RequestBody ExpectedIncomeTotalTableVo expectedIncomeTotalTableVo) throws Exception {
        init(expectedIncomeTotalTableVo);
        List<List<String>> list = bankMyProductService.expectedIncomeTable(expectedIncomeTotalTableVo);

        return Result.sucess(list);
    }

    @PostMapping("expectedIncomeTotal")
    public Result<StackedLineChart> expectedIncomeTotal(@Validated @RequestBody ExpectedIncomeTotalTableVo expectedIncomeTotalTableVo) throws Exception {
        init(expectedIncomeTotalTableVo);
        StackedLineChart list = bankMyProductService.expectedIncomeTotal(expectedIncomeTotalTableVo);

        return Result.sucess(list);
    }

    @PostMapping("expectedIncomeTotalTable")
    public Result<List<List<String>>> expectedIncomeTotalTable(@Validated @RequestBody ExpectedIncomeTotalTableVo expectedIncomeTotalTableVo) throws Exception {
        init(expectedIncomeTotalTableVo);
        List<List<String>> list = bankMyProductService.expectedIncomeTotalTable(expectedIncomeTotalTableVo);
        return Result.sucess(list);
    }

    @PostMapping("getAnalysisTotalVo")
    public Result<List<AnalysisTotalVo>> getAnalysisTotalVo(@Validated @RequestBody ExpectedIncomeTotalTableVo expectedIncomeTotalTableVo) throws Exception {
        init(expectedIncomeTotalTableVo);
        List<AnalysisTotalVo> list = bankMyProductService.getAnalysisTotalVo(expectedIncomeTotalTableVo);
        return Result.sucess(list);
    }

    private void init(ExpectedIncomeTotalTableVo expectedIncomeTotalTableVo) {
        if (null == expectedIncomeTotalTableVo.getStartTime() && null == expectedIncomeTotalTableVo.getEndTime()) {
            if (StringUtils.isNotEmpty(expectedIncomeTotalTableVo.getTime())) {
                int year = Integer.valueOf(expectedIncomeTotalTableVo.getTime().substring(0, 4));
                int month = Integer.valueOf(expectedIncomeTotalTableVo.getTime().substring(5, 7));
                logger.info("{}，年：{}，月：{}", expectedIncomeTotalTableVo.getTime(), year, month);
                expectedIncomeTotalTableVo.setStartTime(TimeUtils.getFirstDayOfMonth(year, month));
                expectedIncomeTotalTableVo.setEndTime(TimeUtils.getLastDayOfMonth(year, month));
            } else {
                expectedIncomeTotalTableVo.setStartTime(TimeUtils.getCurrentYeadFirstDay());
                expectedIncomeTotalTableVo.setEndTime(new Date());
            }
        }
    }
}
