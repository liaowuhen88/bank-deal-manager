package com.wwt.managemail.service.impl;

import com.alibaba.fastjson.JSON;
import com.wwt.managemail.entity.Bank;
import com.wwt.managemail.entity.BankBill;
import com.wwt.managemail.entity.BankMyProduct;
import com.wwt.managemail.enums.TransactionTypeEnum;
import com.wwt.managemail.mapper.BankMyProductMapper;
import com.wwt.managemail.service.BankBillService;
import com.wwt.managemail.service.BankMyProductService;
import com.wwt.managemail.service.BankService;
import com.wwt.managemail.utils.BigDecimalUtils;
import com.wwt.managemail.utils.MoneyUtills;
import com.wwt.managemail.utils.TimeUtils;
import com.wwt.managemail.vo.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BankMyProductServiceImpl implements BankMyProductService {
    public final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private BankMyProductMapper bankMyProductMapper;
    @Autowired
    private BankService bankService;
    @Autowired
    private BankBillService bankBillService;

    @Override
    public int insertSelective(BankMyProduct bankMyProduct) {
        return bankMyProductMapper.insertSelective(bankMyProduct);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int buy(BankMyProduct bankMyProduct) {
        Bank bank = bankService.selectById(bankMyProduct.getBankCardId());
        if (bank.getCashAmount().compareTo(bankMyProduct.getInvestmentAmount()) == -1) {
            throw new RuntimeException("金额不足");
        }
        bankMyProduct.setState(1);
        int code = insertSelective(bankMyProduct);

        BankBill bankBill = new BankBill();
        bankBill.setBankCardId(bankMyProduct.getBankCardId());
        bankBill.setTransactionAmount(bankMyProduct.getInvestmentAmount());
        bankBill.setMyProductId(bankMyProduct.getId());
        bankBill.setTransactionType(TransactionTypeEnum.investment.getCode());
        bankBill.setTransactionTime(bankMyProduct.getBuyingTime());
        bankBill.setRemark(bankMyProduct.getRemark());
        bankService.transaction(bankBill);
        bankBillService.insertSelective(bankBill);
        return code;
    }

    @Override
    public int update(BankMyProduct bankMyProduct) {
        return bankMyProductMapper.updateByPrimaryKeySelective(bankMyProduct);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int redeem(ProductTransaction bankBill) {
        // 获取理财记录
        BankMyProduct bankMyProductDb = bankMyProductMapper.selectByPrimaryKey(bankBill.getMyProductId());
        BigDecimal investmentAmount = bankBill.getInvestmentAmount();
        // 基金赎回，增加利息，修改状态
        BankMyProduct bankMyProduct = new BankMyProduct();
        bankMyProduct.setId(bankBill.getMyProductId());
        bankMyProduct.setTotalEffectiveInterestIncome(bankBill.getTransactionAmount());
        bankMyProduct.setProfitDate(bankBill.getNextProfitDate());

        if (investmentAmount.compareTo(bankMyProductDb.getInvestmentAmount()) == 0) {
            // 全部赎回
            bankMyProduct.setState(2);
        } else if (investmentAmount.compareTo(bankMyProductDb.getInvestmentAmount()) == -1) {
            // 部分赎回  赎回金额小于总金额
            bankMyProduct.setInvestmentAmount(BigDecimalUtils.sub(bankMyProductDb.getInvestmentAmount(), investmentAmount));
        } else {
            //异常
            throw new RuntimeException("赎回金额，已大于投资总金额，请重新填写");
        }
        bankMyProductMapper.transaction(bankMyProduct);
        //账单，先记录利息
        bankBill.setTransactionType(TransactionTypeEnum.investmentIncome.getCode());
        bankBillService.insertSelective(bankBill);
        bankService.transaction(bankBill);
        // 在记录本金

        bankBill.setTransactionAmount(investmentAmount);
        bankBill.setTransactionType(TransactionTypeEnum.investment_redeem_principal.getCode());
        bankBill.setId(null);
        bankBillService.insertSelective(bankBill);
        //银行卡做账
        bankBill.setTransactionAmount(investmentAmount);
        bankService.transaction(bankBill);
        return 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int income(ProductTransaction bankBill) {
        BankMyProduct bankMyProduct = new BankMyProduct();
        bankMyProduct.setId(bankBill.getMyProductId());
        bankMyProduct.setTotalEffectiveInterestIncome(bankBill.getTransactionAmount());
        bankMyProduct.setProfitDate(bankBill.getNextProfitDate());
        bankMyProductMapper.transaction(bankMyProduct);
        bankService.transaction(bankBill);
        bankBillService.insertSelective(bankBill);
        return 0;
    }

    @Override
    public BankMyProduct selectByPrimaryKey(Integer id) {
        return bankMyProductMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<BankMyProduct> selectAll() {
        return bankMyProductMapper.selectAll();
    }

    @Override
    public List<BankMyProductVo> select(BankMyProductQueryVO bankMyProductQueryVO) {
        List<BankMyProduct> list = bankMyProductMapper.selectByBankMyProductQueryVO(bankMyProductQueryVO);
        return getBankMyProductVos(list);
    }

    @Override
    public List<String> selectInterestPaymentMethod() {
        return bankMyProductMapper.selectInterestPaymentMethod();
    }

    @Override
    public List<BankMyProductVo> expireProduct() {
        BankMyProductQueryVO bankMyProductQueryVO = new BankMyProductQueryVO();
        bankMyProductQueryVO.setExpireProductTime(new Date());
        bankMyProductQueryVO.setState(new Integer[]{1});
        List<BankMyProduct> list = bankMyProductMapper.selectByBankMyProductQueryVO(bankMyProductQueryVO);
        return getBankMyProductVos(list);
    }

    @Override
    public List<BankMyProductVo> expireInterest() {
        BankMyProductQueryVO bankMyProductQueryVO = new BankMyProductQueryVO();
        bankMyProductQueryVO.setExpireInterestTime(new Date());
        bankMyProductQueryVO.setState(new Integer[]{1});
        List<BankMyProduct> list = bankMyProductMapper.selectByBankMyProductQueryVO(bankMyProductQueryVO);
        return getBankMyProductVos(list);
    }


    @Override
    public StackedLineChart expectedIncome(ExpectedIncomeTotalTableVo expectedIncomeTotalTableVo) throws Exception {
        //创建图表数据
        StackedLineChart stackedLineChart = new StackedLineChart();
        Map<Integer, Map<String, List<BigDecimal>>> map = getexpectedIncome(expectedIncomeTotalTableVo);

        Map<Integer, List<ExpectedIncomeTotalVo>> maps = coverto(map);

        //List<ExpectedIncomeTotalVo> expectedIncomeTotalVos = bankMyProductMapper.expectedIncome();
        logger.info(JSON.toJSONString(maps));
        List<String> times = initTimes(maps);

        List<Serie> mapSeries = initSeries(times, maps);
        Legend legend = initLegend(mapSeries);

        XAxis xAxis = new XAxis();
        xAxis.setData(times);
        stackedLineChart.setXAxis(xAxis);
        stackedLineChart.setSeries(mapSeries);
        stackedLineChart.setLegend(legend);

        return stackedLineChart;
    }

    @Override
    public List<List<String>> expectedIncomeTable(ExpectedIncomeTotalTableVo expectedIncomeTotalTableVo) throws Exception {
        List<List<String>> list = new ArrayList<>();
        // 获取第一行数据
        List<String> times = TimeUtils.getMonthBetween(expectedIncomeTotalTableVo.getStartTime(), expectedIncomeTotalTableVo.getEndTime());
        //获取理财产品
        BankMyProductQueryVO bankMyProductQueryVO = new BankMyProductQueryVO();
        bankMyProductQueryVO.setState(new Integer[]{1, 2});
        List<BankMyProductVo> bankMyProductVos = select(bankMyProductQueryVO);
        Map<Integer, BankMyProductVo> planVoMap = bankMyProductVos.stream().collect(Collectors.toMap(BankMyProductVo::getId, account -> account));
        //获取收息计划
        List<ExpectedIncomePlanVo> planVos = getExpectedIncomePlan(expectedIncomeTotalTableVo, bankMyProductVos);
        // 每个产品，对应每个日期
        Map<Integer, Map<String, List<BigDecimal>>> maps = getexpectedIncome(planVos);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (Map.Entry<Integer, Map<String, List<BigDecimal>>> entry : maps.entrySet()) {
            List<String> row = new ArrayList<>();
            Integer id = entry.getKey();
            row.add(id.toString());
            row.add(MoneyUtills.toMoney(planVoMap.get(id).getInvestmentAmount()));
            row.add(sdf.format(planVoMap.get(id).getBuyingTime()));
            row.add(sdf.format(planVoMap.get(id).getDueTime()));
            row.add(planVoMap.get(id).getInterestPaymentMethod());
            Map<String, List<BigDecimal>> value = entry.getValue();
            // 单条记录合计
            BigDecimal totalAll = new BigDecimal(0);
            for (String time : times) {
                // 获取当前时间对应数据
                List<BigDecimal> vos = value.get(time);
                BigDecimal total = new BigDecimal(0);
                if (null != vos) {
                    for (BigDecimal expectedInterestIncomeMonth : vos) {
                        total = total.add(expectedInterestIncomeMonth);
                    }
                }
                totalAll = totalAll.add(total);
                row.add(total.toString());
            }
            row.add(row.size(), totalAll.toString());
            list.add(row);
        }


        times.add(0, "产品编号");
        times.add(1, "投资金额");
        times.add(2, "买入时间");
        times.add(3, "到期时间");
        times.add(4, "付息方式");
        times.add(times.size(), "合计");
        list.add(0, times);
        //计算最后一行合计
        List<String> summary = getsummary(list, 5, times.size());
        list.add(summary);
        return list;
    }

    private List<String> getsummary(List<List<String>> list, int start, int count) {
        List<String> summary = new ArrayList<>();
        summary.add("合计");
        summary.add("");
        summary.add("");
        summary.add("");
        summary.add("");
        for (int i = start; i < count; i++) {
            BigDecimal total = new BigDecimal(0);
            for (int j = 1; j < list.size(); j++) {
                List<String> row = list.get(j);
                if (null != row && StringUtils.isNotEmpty(row.get(i))) {
                    total = total.add(new BigDecimal(row.get(i)));
                }
            }
            summary.add(MoneyUtills.toMoney(total));
        }
        return summary;
    }

    private Map<Integer, Map<String, List<BigDecimal>>> getexpectedIncome(ExpectedIncomeTotalTableVo expectedIncomeTotalTableVo) throws Exception {
        List<ExpectedIncomePlanVo> list = getExpectedIncomePlan(expectedIncomeTotalTableVo);
        return getexpectedIncome(list);

    }

    private Map<Integer, Map<String, List<BigDecimal>>> getexpectedIncome(List<ExpectedIncomePlanVo> list) throws Exception {
        //  key 基金id  key 日期  map 收益数组
        Map<Integer, Map<String, List<BigDecimal>>> map = new HashMap();

        if (null != list) {
            for (ExpectedIncomePlanVo vo : list) {
                String time = vo.getTime().substring(0, 7);
                // 设置为月份
                addExpectedInterestIncomeMonth(map, time, vo);
            }
        }

        logger.info(JSON.toJSONString(map));

        return map;

    }



    @Override
    public List<ExpectedIncomePlanVo> getExpectedIncomePlan(ExpectedIncomeTotalTableVo expectedIncomeTotalTableVo, List<BankMyProductVo> list) throws Exception {
        List<ExpectedIncomePlanVo> planVos = new ArrayList<>();

        //  key 基金id  key 日期  map 收益数组
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if (null != list) {
            for (BankMyProductVo vo : list) {
                Date start = vo.getInterestStartTime();
                Date end = vo.getDueTime();
                Date base = vo.getProfitDate();
                String startTime = sdf.format(start);
                // 设置为月份
                if ("月付".equals(vo.getInterestPaymentMethod())) {
                    List<String> times = TimeUtils.getMonthBetween(start, end, base, sdf);
                    logger.info("{}：{}", vo.getInterestPaymentMethod(), JSON.toJSONString(times));
                    if (null != times) {
                        for (String time : times) {
                            ExpectedIncomePlanVo planVo = getExpectedIncomePlanVo(startTime, time, 30, vo);
                            startTime = time;
                            planVos.add(planVo);
                        }
                    }
                    // 获取所有的月份  start---end
                } else if ("季付".equals(vo.getInterestPaymentMethod())) {
                    List<String> times = TimeUtils.getMonthBetween(start, end, base, sdf, 3);
                    logger.info("季付：{}", JSON.toJSONString(times));
                    if (null != times) {
                        for (String time : times) {
                            ExpectedIncomePlanVo planVo = getExpectedIncomePlanVo(startTime, time, 30 * 3, vo);
                            startTime = time;
                            planVos.add(planVo);
                        }
                    }
                    // 获取所有的月份  start---end
                } else if ("半年付".equals(vo.getInterestPaymentMethod())) {
                    List<String> times = TimeUtils.getMonthBetween(start, end, base, sdf, 6);
                    logger.info("半年付：{}", JSON.toJSONString(times));
                    if (null != times) {
                        for (String time : times) {
                            ExpectedIncomePlanVo planVo = getExpectedIncomePlanVo(startTime, time, 30 * 6, vo);
                            startTime = time;
                            planVos.add(planVo);
                        }
                    }
                    // 获取所有的月份  start---end
                } else if ("一次性".equals(vo.getInterestPaymentMethod())) {
                    String time = sdf.format(vo.getProfitDate());
                    ExpectedIncomePlanVo planVo = getExpectedIncomePlanVo(time, vo);
                    planVos.add(planVo);
                }
            }

            if (null != expectedIncomeTotalTableVo.getStartTime() && null != expectedIncomeTotalTableVo.getEndTime()) {
                String sTime = sdf.format(expectedIncomeTotalTableVo.getStartTime());
                String eTime = sdf.format(expectedIncomeTotalTableVo.getEndTime());
                planVos = planVos.stream()
                        .filter((ExpectedIncomePlanVo s) -> s.getTime().compareTo(sTime) >= 0)
                        .filter((ExpectedIncomePlanVo s) -> s.getTime().compareTo(eTime) <= 0)
                        .collect(Collectors.toList());
            }

        }
        return planVos;

    }

    @Override
    public List<ExpectedIncomePlanVo> getExpectedIncomePlan(ExpectedIncomeTotalTableVo expectedIncomeTotalTableVo) throws Exception {
        // 获取产品数据
        BankMyProductQueryVO bankMyProductQueryVO = new BankMyProductQueryVO();
        bankMyProductQueryVO.setState(new Integer[]{1, 2});
        List<BankMyProductVo> list = select(bankMyProductQueryVO);
        return getExpectedIncomePlan(expectedIncomeTotalTableVo, list);
    }

    /**
     * @param startTime 计息开始时间
     * @param time      收息日期
     * @param vo
     * @return
     */
    private ExpectedIncomePlanVo getExpectedIncomePlanVo(String startTime, String time, int totalDays, BankMyProductVo vo) throws ParseException {
        ExpectedIncomePlanVo planVo = new ExpectedIncomePlanVo();
        planVo.setId(vo.getId());
        planVo.setTime(time);
        BigDecimal expectedInterestIncomeMonth = getRealExpectedInterestIncomeMonth(startTime, time, totalDays, vo.getExpectedInterestIncomeMonth());
        planVo.setExpectedInterestIncomeMonth(expectedInterestIncomeMonth);
        planVo.setInterestPaymentMethod(vo.getInterestPaymentMethod());
        return planVo;
    }

    private ExpectedIncomePlanVo getExpectedIncomePlanVo(String time, BankMyProductVo vo) throws ParseException {
        ExpectedIncomePlanVo planVo = new ExpectedIncomePlanVo();
        planVo.setId(vo.getId());
        planVo.setTime(time);
        planVo.setExpectedInterestIncomeMonth(vo.getExpectedInterestIncomeMonth());
        planVo.setInterestPaymentMethod(vo.getInterestPaymentMethod());
        return planVo;
    }

    private BigDecimal getRealExpectedInterestIncomeMonth(String startTime, String time, int totalDays, BigDecimal expectedInterestIncomeMonth) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        int dates = TimeUtils.daysBetween(sdf.parse(startTime), sdf.parse(time));
        // 不满一个月
        if (dates < totalDays) {
            BigDecimal days = BigDecimalUtils.div(expectedInterestIncomeMonth.toString(), totalDays + "");
            return BigDecimalUtils.mul(days.toString(), dates + "");
        } else {
            return expectedInterestIncomeMonth;
        }

    }

    @Override
    public StackedLineChart expectedIncomeTotal(ExpectedIncomeTotalTableVo expectedIncomeTotalTableVo) throws Exception {
        //创建图表数据
        StackedLineChart stackedLineChart = new StackedLineChart();
        List<String> times = TimeUtils.getMonthBetween(expectedIncomeTotalTableVo.getStartTime(), expectedIncomeTotalTableVo.getEndTime());
        //  key 基金id  key 日期  map 收益数组
        Map<String, List<BigDecimal>> map = getExpectedIncomeTotalVos(expectedIncomeTotalTableVo);
        List<ExpectedIncomeTotalVo> vos = covertoList(map);
        //List<ExpectedIncomeTotalVo> expectedIncomeTotalVos = bankMyProductMapper.expectedIncome();
        logger.info(JSON.toJSONString(vos));
        //times = filter(times, bankBillQuery);
        List<Serie> mapSeries = initSeries(times, vos);
        Legend legend = initLegend(mapSeries);

        XAxis xAxis = new XAxis();
        xAxis.setData(times);
        stackedLineChart.setXAxis(xAxis);
        stackedLineChart.setSeries(mapSeries);
        stackedLineChart.setLegend(legend);

        return stackedLineChart;
    }

    private Map<String, List<BigDecimal>> getExpectedIncomeTotalVos(ExpectedIncomeTotalTableVo expectedIncomeTotalTableVo) throws Exception {
        // 生成横轴数据
        List<ExpectedIncomePlanVo> planVos = getExpectedIncomePlan(expectedIncomeTotalTableVo);
        return getExpectedIncomeTotalVos(planVos);

    }

    private Map<String, List<BigDecimal>> getExpectedIncomeTotalVos(List<ExpectedIncomePlanVo> list) throws Exception {

        // key 日期  map 收益数组
        Map<String, List<BigDecimal>> map = new HashMap();
        if (null != list) {
            for (ExpectedIncomePlanVo vo : list) {
                // 设置为月份
                String time = vo.getTime().substring(0, 7);
                addExpectedInterestIncomeMonthTotal(map, time, vo);
            }
        }
        logger.info(JSON.toJSONString(map));

       /* if (StringUtils.isNotEmpty(expectedIncomeTotalTableVo.getStartTime()) && StringUtils.isNotEmpty(expectedIncomeTotalTableVo.getEndTime())) {
            vos = vos.stream()
                    .filter((ExpectedIncomeTotalVo s) -> s.getProfitDate().compareTo(expectedIncomeTotalTableVo.getStartTime()) >= 0)
                    .filter((ExpectedIncomeTotalVo s) -> s.getProfitDate().compareTo(expectedIncomeTotalTableVo.getEndTime()) <= 0)
                    .collect(Collectors.toList());
        }*/
        return map;

    }


    private Map<String, List<BigDecimal>> getInvestmentAmount(List<BankMyProductVo> list) throws Exception {

        // key 日期  map 收益数组
        Map<String, List<BigDecimal>> map = new HashMap();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");

        if (null != list) {
            for (BankMyProductVo vo : list) {
                // 设置为月份
                String time = sdf.format(vo.getDueTime());
                List<BigDecimal> vos = map.get(time);
                if (null == vos) {
                    vos = new ArrayList<>();
                    map.put(time, vos);
                }
                vos.add(vo.getInvestmentAmount());
            }
        }
        logger.info(JSON.toJSONString(map));

       /* if (StringUtils.isNotEmpty(expectedIncomeTotalTableVo.getStartTime()) && StringUtils.isNotEmpty(expectedIncomeTotalTableVo.getEndTime())) {
            vos = vos.stream()
                    .filter((ExpectedIncomeTotalVo s) -> s.getProfitDate().compareTo(expectedIncomeTotalTableVo.getStartTime()) >= 0)
                    .filter((ExpectedIncomeTotalVo s) -> s.getProfitDate().compareTo(expectedIncomeTotalTableVo.getEndTime()) <= 0)
                    .collect(Collectors.toList());
        }*/
        return map;

    }


    @Override
    public List<List<String>> expectedIncomeTotalTable(ExpectedIncomeTotalTableVo expectedIncomeTotalTableVo) throws Exception {
        List<List<String>> list = new ArrayList<>();
        // 获取第一行数据
        List<String> times = TimeUtils.getMonthBetween(expectedIncomeTotalTableVo.getStartTime(), expectedIncomeTotalTableVo.getEndTime());

        // 获取所有理财产品
        BankMyProductQueryVO bankMyProductQueryVO = new BankMyProductQueryVO();
        bankMyProductQueryVO.setState(new Integer[]{1, 2});
        List<BankMyProductVo> bankMyProductVos = select(bankMyProductQueryVO);

        List<ExpectedIncomePlanVo> planVos = getExpectedIncomePlan(expectedIncomeTotalTableVo, bankMyProductVos);

        // 每个产品，对应每个日期,预期收益
        Map<String, List<BigDecimal>> maps = getExpectedIncomeTotalVos(planVos);
        // 每个产品，对应每个日期,本金赎回
        Map<String, List<BigDecimal>> investmentAmountMaps = getInvestmentAmount(bankMyProductVos);

        // 获取实际收益
        BankBillQuery bankBillQuery = new BankBillQuery();
        List<BankBillTotalVo> bankBillTotalVos = bankBillService.totalByMonth(bankBillQuery);

        Map<Integer, Map<String, BankBillTotalVo>> map = bankBillService.getBankBillTotalVoMap(bankBillTotalVos);
        Map<String, BankBillTotalVo> bankBillTotalVoMap = map.get(TransactionTypeEnum.investmentIncome.getCode());
        // 获取实际理财赎回
        Map<String, BankBillTotalVo> investmentRedeemPrincipalMap = map.get(TransactionTypeEnum.investment_redeem_principal.getCode());

        List<String> row = new ArrayList<>();
        row.add("预期待收益");

        List<String> investmentAmountRow = new ArrayList<>();
        investmentAmountRow.add("预期本金赎回");

        List<String> investmentIncomeRow = new ArrayList<>();
        investmentIncomeRow.add("实际已收益");

        List<String> investmentRedeemPrincipalRow = new ArrayList<>();
        investmentRedeemPrincipalRow.add("实际赎回本金");


        BigDecimal totalAll = new BigDecimal(0);
        BigDecimal investmentAmountTotalAll = new BigDecimal(0);
        BigDecimal investmentIncomeTotalAll = new BigDecimal(0);
        BigDecimal investmentRedeemPrincipalTotalAll = new BigDecimal(0);

        for (String time : times) {
            // 获取当前时间对应数据，预期收益
            List<BigDecimal> vos = maps.get(time);
            BigDecimal total = new BigDecimal(0);
            if (null != vos) {
                for (BigDecimal expectedInterestIncomeMonth : vos) {
                    total = total.add(expectedInterestIncomeMonth);
                }
            }
            totalAll = totalAll.add(total);
            row.add(total.toString());
            // 预期本金
            List<BigDecimal> investmentAmounts = investmentAmountMaps.get(time);
            BigDecimal totalInvestmentAmounts = new BigDecimal(0);
            if (null != investmentAmounts) {
                for (BigDecimal expectedInterestIncomeMonth : investmentAmounts) {
                    totalInvestmentAmounts = totalInvestmentAmounts.add(expectedInterestIncomeMonth);
                }
            }
            investmentAmountTotalAll = investmentAmountTotalAll.add(totalInvestmentAmounts);
            investmentAmountRow.add(totalInvestmentAmounts.toString());

            // 实际收益
            BankBillTotalVo bankBillTotalVo = bankBillTotalVoMap.get(time);
            if (null == bankBillTotalVo) {
                investmentIncomeRow.add("0");
            } else {
                investmentIncomeTotalAll = investmentIncomeTotalAll.add(bankBillTotalVo.getTotalTransactionAmount());
                investmentIncomeRow.add(bankBillTotalVo.getTotalTransactionAmount().toString());
            }
            //赎回本金
            BankBillTotalVo investmentRedeemPrincipal = investmentRedeemPrincipalMap.get(time);
            if (null == investmentRedeemPrincipal) {
                investmentRedeemPrincipalRow.add("0");
            } else {
                investmentRedeemPrincipalTotalAll = investmentRedeemPrincipalTotalAll.add(investmentRedeemPrincipal.getTotalTransactionAmount());
                investmentRedeemPrincipalRow.add(investmentRedeemPrincipal.getTotalTransactionAmount().toString());
            }
        }
        row.add(row.size(), totalAll.toString());
        investmentIncomeRow.add(investmentIncomeRow.size(), investmentIncomeTotalAll.toString());
        investmentAmountRow.add(investmentAmountRow.size(), investmentAmountTotalAll.toString());
        investmentRedeemPrincipalRow.add(investmentRedeemPrincipalRow.size(), investmentRedeemPrincipalTotalAll.toString());

        list.add(row);
        list.add(investmentIncomeRow);
        list.add(investmentAmountRow);
        list.add(investmentRedeemPrincipalRow);

        times.add(0, "时间");
        times.add(times.size(), "合计");
        list.add(0, times);
        //计算最后一行合计
        //List<String> summary = getsummary(list, times.size());
        //list.add(summary);

        return list;
    }

    @Override
    public List<AnalysisTotalVo> getAnalysisTotalVo(ExpectedIncomeTotalTableVo expectedIncomeTotalTableVo) throws Exception {
        List<AnalysisTotalVo> list = new ArrayList<>();
        // 获取第一行数据
        List<String> times = TimeUtils.getMonthBetween(expectedIncomeTotalTableVo.getStartTime(), expectedIncomeTotalTableVo.getEndTime());

        // 获取所有理财产品
        BankMyProductQueryVO bankMyProductQueryVO = new BankMyProductQueryVO();
        bankMyProductQueryVO.setState(new Integer[]{1, 2});
        List<BankMyProductVo> bankMyProductVos = select(bankMyProductQueryVO);

        List<ExpectedIncomePlanVo> planVos = getExpectedIncomePlan(expectedIncomeTotalTableVo, bankMyProductVos);

        // 每个产品，对应每个日期,预期收益
        Map<String, List<BigDecimal>> maps = getExpectedIncomeTotalVos(planVos);
        // 每个产品，对应每个日期,本金赎回
        Map<String, List<BigDecimal>> investmentAmountMaps = getInvestmentAmount(bankMyProductVos);

        // 获取实际收益
        BankBillQuery bankBillQuery = new BankBillQuery();
        List<BankBillTotalVo> bankBillTotalVos = bankBillService.totalByMonth(bankBillQuery);
        Map<Integer, Map<String, BankBillTotalVo>> map = bankBillService.getBankBillTotalVoMap(bankBillTotalVos);
        Map<String, BankBillTotalVo> bankBillTotalVoMap = map.get(TransactionTypeEnum.investmentIncome.getCode());
        // 获取实际理财赎回
        Map<String, BankBillTotalVo> investmentRedeemPrincipalMap = map.get(TransactionTypeEnum.investment_redeem_principal.getCode());
        for (String time : times) {
            AnalysisTotalVo vo = new AnalysisTotalVo();
            vo.setTime(time);
            // 获取当前时间对应数据，预期收益
            List<BigDecimal> vos = maps.get(time);
            BigDecimal total = new BigDecimal(0);
            if (null != vos) {
                for (BigDecimal expectedInterestIncomeMonth : vos) {
                    total = total.add(expectedInterestIncomeMonth);
                }
            }

            vo.setExpectedInterestIncome(total);
            // 预期本金
            List<BigDecimal> investmentAmounts = investmentAmountMaps.get(time);
            BigDecimal totalInvestmentAmounts = new BigDecimal(0);
            if (null != investmentAmounts) {
                for (BigDecimal expectedInterestIncomeMonth : investmentAmounts) {
                    totalInvestmentAmounts = totalInvestmentAmounts.add(expectedInterestIncomeMonth);
                }
            }
            vo.setInvestmentAmount(totalInvestmentAmounts);

            // 实际收益
            BankBillTotalVo bankBillTotalVo = bankBillTotalVoMap.get(time);
            if (null == bankBillTotalVo) {
                vo.setInvestmentIncome(new BigDecimal("0"));
            } else {
                vo.setInvestmentIncome(bankBillTotalVo.getTotalTransactionAmount());
            }
            //赎回本金
            BankBillTotalVo investmentRedeemPrincipal = investmentRedeemPrincipalMap.get(time);
            if (null == investmentRedeemPrincipal) {
                vo.setInvestmentRedeemPrincipal(new BigDecimal("0"));
            } else {
                vo.setInvestmentRedeemPrincipal(investmentRedeemPrincipal.getTotalTransactionAmount());
            }

            list.add(vo);
        }
        return list;

    }

    private List<ExpectedIncomeTotalVo> covertoList(Map<String, List<BigDecimal>> map) {
        List<ExpectedIncomeTotalVo> expectedIncomeTotalVos = new ArrayList<>();
        for (Map.Entry<String, List<BigDecimal>> entry : map.entrySet()) {
            ExpectedIncomeTotalVo vo = new ExpectedIncomeTotalVo();
            vo.setProfitDate(entry.getKey());
            BigDecimal total = new BigDecimal(0);
            List<BigDecimal> vos = entry.getValue();
            if (null != vos) {
                for (BigDecimal expectedInterestIncomeMonth : vos) {
                    total = total.add(expectedInterestIncomeMonth);
                }
                vo.setExpectedInterestIncomeMonth(total);
            }
            expectedIncomeTotalVos.add(vo);
        }
        Collections.sort(expectedIncomeTotalVos, (o1, o2) -> {
            int i = o1.getProfitDate().compareTo(o2.getProfitDate());
            return i;
        });

        return expectedIncomeTotalVos;
    }

    private Map<Integer, List<ExpectedIncomeTotalVo>> coverto(Map<Integer, Map<String, List<BigDecimal>>> map) {
        Map<Integer, List<ExpectedIncomeTotalVo>> ret = new HashMap<>();
        for (Map.Entry<Integer, Map<String, List<BigDecimal>>> entry : map.entrySet()) {
            List<ExpectedIncomeTotalVo> list = covertoList(entry.getValue());
            ret.put(entry.getKey(), list);
        }

        return ret;
    }

    private void addExpectedInterestIncomeMonth(Map<Integer, Map<String, List<BigDecimal>>> map, String time, ExpectedIncomePlanVo vo) {
        Map<String, List<BigDecimal>> idMap = map.get(vo.getId());
        if (null == idMap) {
            idMap = new HashMap<>();
            map.put(vo.getId(), idMap);
        }
        List<BigDecimal> vos = idMap.get(time);
        if (null == vos) {
            vos = new ArrayList<>();
            idMap.put(time, vos);
        }
        vos.add(vo.getExpectedInterestIncomeMonth());
    }

    private void addExpectedInterestIncomeMonthTotal(Map<String, List<BigDecimal>> map, String time, ExpectedIncomePlanVo vo) {
        List<BigDecimal> vos = map.get(time);
        if (null == vos) {
            vos = new ArrayList<>();
            map.put(time, vos);
        }
        vos.add(vo.getExpectedInterestIncomeMonth());
    }

    public List<String> initTimes(Map<Integer, List<ExpectedIncomeTotalVo>> maps) {
        List<ExpectedIncomeTotalVo> expectedIncomeTotalVos = new ArrayList<>();
        for (Map.Entry<Integer, List<ExpectedIncomeTotalVo>> entry : maps.entrySet()) {
            expectedIncomeTotalVos.addAll(entry.getValue());
        }
        Set<String> set = new HashSet<>();
        for (ExpectedIncomeTotalVo vo : expectedIncomeTotalVos) {
            set.add(vo.getProfitDate());
        }
        List<String> time = new ArrayList<>(set);
        Collections.sort(time, (o1, o2) -> {
            int i = o1.compareTo(o2);
            return i;
        });
        return time;

    }


    public List<String> initTimes(List<ExpectedIncomeTotalVo> expectedIncomeTotalVos) {
        HashSet set = new HashSet();
        for (ExpectedIncomeTotalVo vo : expectedIncomeTotalVos) {
            set.add(vo.getProfitDate());
        }
        List<String> time = new ArrayList<>(set);
        Collections.sort(time, (o1, o2) -> {
            int i = o1.compareTo(o2);
            return i;
        });
        return time;

    }

    public List<Serie> initSeries(List<String> times, Map<Integer, List<ExpectedIncomeTotalVo>> maps) {
        List<Serie> list = new ArrayList<>();
        for (Map.Entry<Integer, List<ExpectedIncomeTotalVo>> entry : maps.entrySet()) {
            List<ExpectedIncomeTotalVo> expectedIncomeTotalVos = entry.getValue();
            Map<String, ExpectedIncomeTotalVo> mappedMovies = expectedIncomeTotalVos.stream().collect(
                    Collectors.toMap(ExpectedIncomeTotalVo::getProfitDate, p -> p));

            List<String> investmentIncomeData = new ArrayList<>();
            for (String time : times) {
                if (null == mappedMovies.get(time)) {
                    investmentIncomeData.add("");
                } else {
                    investmentIncomeData.add(mappedMovies.get(time).getExpectedInterestIncomeMonth().toString());
                }
            }

            Serie investmentIncome = new Serie();
            investmentIncome.setName(entry.getKey() + "预期收益");
            investmentIncome.setData(investmentIncomeData);
            list.add(investmentIncome);
        }
        return list;

    }

    public List<Serie> initSeries(List<String> times, List<ExpectedIncomeTotalVo> expectedIncomeTotalVos) {
        List<Serie> list = new ArrayList<>();
        Map<String, ExpectedIncomeTotalVo> mappedMovies = expectedIncomeTotalVos.stream().collect(
                Collectors.toMap(ExpectedIncomeTotalVo::getProfitDate, p -> p));

        List<String> investmentIncomeData = new ArrayList<>();
        for (String time : times) {
            if (null == mappedMovies.get(time)) {
                investmentIncomeData.add("");
            } else {
                investmentIncomeData.add(mappedMovies.get(time).getExpectedInterestIncomeMonth().toString());
            }
        }

        Serie investmentIncome = new Serie();
        investmentIncome.setName("总预期收益");
        investmentIncome.setData(investmentIncomeData);
        list.add(investmentIncome);

        return list;

    }

    public Legend initLegend(List<Serie> series) {
        Legend legend = new Legend();
        List<String> list = new ArrayList<>();
        legend.setData(list);
        for (Serie serie : series) {
            list.add(serie.getName());
        }
        return legend;

    }

    List<BankMyProductVo> getBankMyProductVos(List<BankMyProduct> list) {
        if (null != list) {
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
            return res;
        }
        return null;
    }
}
