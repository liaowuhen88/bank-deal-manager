package com.wwt.managemail.service;

import com.wwt.managemail.entity.BankMyProduct;
import com.wwt.managemail.vo.*;

import java.util.List;

public interface BankMyProductService {
    /**
     * 保存理财数据
     *
     * @param bankMyProduct
     * @return
     */
    int insertSelective(BankMyProduct bankMyProduct);

    int buy(BankMyProduct bankMyProduct);

    int update(BankMyProduct bankMyProduct);

    /**
     * 赎回
     *
     * @param productTransaction
     * @return
     */
    int redeem(ProductTransaction productTransaction);

    int income(ProductTransaction productTransaction);

    BankMyProduct selectByPrimaryKey(Integer id);

    List<BankMyProduct> selectAll();

    List<BankMyProductVo> select(BankMyProductQueryVO bankMyProductQueryVO);

    List<String> selectInterestPaymentMethod();

    List<BankMyProductVo> expireProduct();

    /**
     * 到期利息
     *
     * @return
     */
    List<BankMyProductVo> expireInterest();

    /**
     * 获取还款计划
     *
     * @return
     * @throws Exception
     */
    List<ExpectedIncomePlanVo> getExpectedIncomePlan(ExpectedIncomeTotalTableVo expectedIncomeTotalTableVo) throws Exception;

    List<ExpectedIncomePlanVo> getExpectedIncomePlan(ExpectedIncomeTotalTableVo expectedIncomeTotalTableVo, List<BankMyProductVo> list) throws Exception;

    StackedLineChart expectedIncome(ExpectedIncomeTotalTableVo expectedIncomeTotalTableVo) throws Exception;

    List<List<String>> expectedIncomeTable(ExpectedIncomeTotalTableVo expectedIncomeTotalTableVo) throws Exception;

    StackedLineChart expectedIncomeTotal(ExpectedIncomeTotalTableVo expectedIncomeTotalTableVo) throws Exception;

    List<List<String>> expectedIncomeTotalTable(ExpectedIncomeTotalTableVo expectedIncomeTotalTableVo) throws Exception;

    List<AnalysisTotalVo> getAnalysisTotalVo(ExpectedIncomeTotalTableVo expectedIncomeTotalTableVo) throws Exception;





}
