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
import com.wwt.managemail.utils.TimeUtils;
import com.wwt.managemail.vo.*;
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
        // 基金赎回，增加利息，修改状态
        BankMyProduct bankMyProduct = new BankMyProduct();
        bankMyProduct.setId(bankBill.getMyProductId());
        bankMyProduct.setTotalEffectiveInterestIncome(bankBill.getTransactionAmount());
        bankMyProduct.setProfitDate(bankBill.getNextProfitDate());
        bankMyProduct.setState(2);
        bankMyProductMapper.transaction(bankMyProduct);
        //账单，先记录利息
        bankBill.setTransactionType(TransactionTypeEnum.investmentIncome.getCode());
        bankBillService.insertSelective(bankBill);
        bankService.transaction(bankBill);
        // 在记录本金
        BankMyProduct bankMyProductDb = bankMyProductMapper.selectByPrimaryKey(bankBill.getMyProductId());
        bankBill.setTransactionAmount(bankMyProductDb.getInvestmentAmount());
        bankBill.setTransactionType(TransactionTypeEnum.investment_redeem_principal.getCode());
        bankBill.setId(null);
        bankBillService.insertSelective(bankBill);
        //银行卡做账
        bankBill.setTransactionAmount(bankMyProductDb.getInvestmentAmount());
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
        List<BankMyProduct> list = bankMyProductMapper.selectByBankMyProductQueryVO(bankMyProductQueryVO);
        return getBankMyProductVos(list);
    }

    @Override
    public List<BankMyProductVo> expireInterest() {
        BankMyProductQueryVO bankMyProductQueryVO = new BankMyProductQueryVO();
        bankMyProductQueryVO.setExpireInterestTime(new Date());
        bankMyProductQueryVO.setState(1);
        List<BankMyProduct> list = bankMyProductMapper.selectByBankMyProductQueryVO(bankMyProductQueryVO);
        return getBankMyProductVos(list);
    }


    @Override
    public StackedLineChart expectedIncome(BankBillQuery bankBillQuery) throws Exception {
        //创建图表数据
        StackedLineChart stackedLineChart = new StackedLineChart();
        // 生成横轴数据
        BankMyProductQueryVO bankMyProductQueryVO = new BankMyProductQueryVO();
        bankMyProductQueryVO.setState(1);
        List<BankMyProductVo> list = select(bankMyProductQueryVO);
        //  key 基金id  key 日期  map 收益数组
        Map<Integer, Map<String, List<BigDecimal>>> map = new HashMap();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");

        if (null != list) {
            for (BankMyProductVo vo : list) {
                // 设置为月份
                logger.info("类型：{}", vo.getInterestPaymentMethod());
                String start = sdf.format(vo.getProfitDate());
                if ("月付".equals(vo.getInterestPaymentMethod())) {
                    List<String> times = TimeUtils.getMonthBetween(vo.getProfitDate(), vo.getDueTime());
                    //logger.info(JSON.toJSONString(times));
                    if (null != times) {
                        for (String time : times) {
                            addExpectedInterestIncomeMonth(map, time, vo);
                        }
                    }
                    // 获取所有的月份  start---end
                } else if ("季付".equals(vo.getInterestPaymentMethod())) {
                    List<String> times = TimeUtils.getMonthBetween(vo.getProfitDate(), vo.getDueTime(), 3);
                    logger.info("季付：{}", JSON.toJSONString(times));
                    if (null != times) {
                        for (String time : times) {
                            addExpectedInterestIncomeMonth(map, time, vo);
                        }
                    }
                    // 获取所有的月份  start---end
                } else if ("半年付".equals(vo.getInterestPaymentMethod())) {
                    List<String> times = TimeUtils.getMonthBetween(vo.getProfitDate(), vo.getDueTime(), 6);
                    logger.info("半年付：{}", JSON.toJSONString(times));
                    if (null != times) {
                        for (String time : times) {
                            addExpectedInterestIncomeMonth(map, time, vo);
                        }
                    }
                    // 获取所有的月份  start---end
                } else if ("一次性".equals(vo.getInterestPaymentMethod())) {
                    addExpectedInterestIncomeMonth(map, start, vo);
                }
            }
        }

        logger.info(JSON.toJSONString(map));
        Map<Integer, List<ExpectedIncomeTotalVo>> maps = coverto(map);
        //List<ExpectedIncomeTotalVo> expectedIncomeTotalVos = bankMyProductMapper.expectedIncome();
        logger.info(JSON.toJSONString(maps));
        List<String> times = initTimes(maps);
        times = filter(times, bankBillQuery);
        List<Serie> mapSeries = initSeries(times, maps);
        Legend legend = initLegend(mapSeries);

        XAxis xAxis = new XAxis();
        xAxis.setData(times);
        stackedLineChart.setXAxis(xAxis);
        stackedLineChart.setSeries(mapSeries);
        stackedLineChart.setLegend(legend);

        return stackedLineChart;
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


    private List<String> filter(List<String> tims, BankBillQuery bankBillQuery) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        if (null != bankBillQuery.getStartTime() && null != bankBillQuery.getEndTime()) {
            tims = tims.stream()
                    .filter((String s) -> {
                        try {
                            return sdf.parse(s).after(bankBillQuery.getStartTime());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        return false;
                    })
                    .filter((String s) -> {
                        try {
                            return sdf.parse(s).before(bankBillQuery.getEndTime());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        return false;
                    })
                    .collect(Collectors.toList());
        }
        return tims;
    }

    private void addExpectedInterestIncomeMonth(Map<Integer, Map<String, List<BigDecimal>>> map, String time, BankMyProductVo vo) {
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
