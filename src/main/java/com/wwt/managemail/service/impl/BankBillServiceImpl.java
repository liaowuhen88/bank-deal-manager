package com.wwt.managemail.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.wwt.managemail.entity.BankBill;
import com.wwt.managemail.enums.TransactionTypeEnum;
import com.wwt.managemail.mapper.BankBillMapper;
import com.wwt.managemail.service.BankBillService;
import com.wwt.managemail.service.BankService;
import com.wwt.managemail.utils.TimeUtils;
import com.wwt.managemail.vo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class BankBillServiceImpl implements BankBillService {
    public final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private BankBillMapper bankBillMapper;

    @Autowired
    private BankService bankService;

    @Override
    public int insertSelective(BankBill bankBill) {
        bankBill.setCreator("admin");
        bankBill.setCreateTime(new Date());
        return bankBillMapper.insertSelective(bankBill);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int transaction(BankBill bankBill) {
        bankService.transaction(bankBill);
        insertSelective(bankBill);
        //如果是转账,需要给转帐方增加记录
        if (TransactionTypeEnum.transfer_out.getCode() == bankBill.getTransactionType()) {
            BankBill bill = new BankBill();
            BeanUtils.copyProperties(bankBill, bill);
            bill.setTransactionType(TransactionTypeEnum.transfer_in.getCode());
            Integer bankCardId = bankBill.getBankCardId();
            bill.setBankCardId(bankBill.getTransferCard());
            bill.setId(null);
            bill.setTransferCard(bankCardId);
            insertSelective(bill);
            bankService.transaction(bill);
        }
        return 0;
    }

    @Override
    public Page<BankBillVo> query(BankBillQuery bankBillQuery) {
        PageHelper.startPage(bankBillQuery.getPage(), bankBillQuery.getPageSize());
        return bankBillMapper.query(bankBillQuery);
    }

    @Override
    public List<BankBillVo> queryNoPage(QueryByTimeVo queryByTimeVo) {
        BankBillQuery bankBillQuery = new BankBillQuery();
        bankBillQuery.setTransactionTypes(queryByTimeVo.getTransactionTypes());
        int month = Integer.valueOf(queryByTimeVo.getTime().substring(5, 7));
        logger.info("{}:{}", queryByTimeVo.getTime(), month);
        bankBillQuery.setStartTime(TimeUtils.getFirstDayOfMonth(month));
        bankBillQuery.setEndTime(TimeUtils.getLastDayOfMonth(month));
        return bankBillMapper.query(bankBillQuery);
    }

    @Override
    public BankBillVo queryLaste(BankBillQuery bankBillQuery) {
        Page<BankBillVo> list = bankBillMapper.query(bankBillQuery);
        if (null != list) {
            return list.get(0);
        }
        return null;
    }

    @Override
    public List<BankBillTotalVo> queryCurrentYearTotal() {
        BankBillQuery bankBillQuery = new BankBillQuery();
        bankBillQuery.setStartTime(TimeUtils.getCurrentYeadFirstDay());
        bankBillQuery.setEndTime(new Date());
        return bankBillMapper.queryTotal(bankBillQuery);
    }

    @Override
    public List<BankBillTotalVo> totalByMonth(BankBillQuery bankBillQuery) {
        return bankBillMapper.totalByMonth(bankBillQuery);
    }


    @Override
    public StackedLineChart totalByMonthEchart(BankBillQuery bankBillQuery) throws Exception {
        if (null == bankBillQuery.getStartTime() && null == bankBillQuery.getEndTime()) {
            bankBillQuery.setStartTime(TimeUtils.getCurrentYeadFirstDay());
            bankBillQuery.setEndTime(new Date());
        }
        //创建图表数据
        StackedLineChart stackedLineChart = new StackedLineChart();
        // 生成横轴数据
        List<String> times = TimeUtils.getMonthBetween(bankBillQuery.getStartTime(), bankBillQuery.getEndTime());
        List<Serie> mapSeries = initSeries(times);
        Legend legend = initLegend(mapSeries);

        XAxis xAxis = new XAxis();
        xAxis.setData(times);
        stackedLineChart.setXAxis(xAxis);
        stackedLineChart.setSeries(mapSeries);
        stackedLineChart.setLegend(legend);

        List<BankBillTotalVo> list = totalByMonth(bankBillQuery);
        logger.info(JSON.toJSONString(list));
        Map<Integer, Map<String, BankBillTotalVo>> map = getBankBillTotalVoMap(list);
        logger.info(JSON.toJSONString(map));
        if (null != mapSeries) {
            Iterator<Serie> iterator = mapSeries.iterator();
            while (iterator.hasNext()) {
                Serie serie = iterator.next();
                Map<String, BankBillTotalVo> bankBillTotalVoMap = map.get(serie.getKey());
                List<String> date = serie.getData();

                for (String time : times) {
                    if (null == bankBillTotalVoMap) {
                        date.add("0");
                    } else {
                        BankBillTotalVo bankBillTotalVo = bankBillTotalVoMap.get(time);
                        if (null == bankBillTotalVo) {
                            date.add("0");
                        } else {
                            date.add(bankBillTotalVo.getTotalTransactionAmount().toString());
                        }
                    }
                }
            }
            return stackedLineChart;
        }

        return null;
    }

    public List<Serie> initSeries(List<String> times) {
        List<Serie> list = new ArrayList<>();

        List<String> investmentIncomeData = new ArrayList<>(times.size());
        Serie investmentIncome = new Serie();
        investmentIncome.setName(TransactionTypeEnum.investmentIncome.getMsg());
        investmentIncome.setData(investmentIncomeData);
        investmentIncome.setKey(TransactionTypeEnum.investmentIncome.getCode());
        list.add(investmentIncome);

        Serie pay = new Serie();
        List<String> payData = new ArrayList<>(times.size());
        pay.setName(TransactionTypeEnum.pay.getMsg());
        pay.setKey(TransactionTypeEnum.pay.getCode());
        pay.setData(payData);
        list.add(pay);
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



    public Map<Integer, Map<String, BankBillTotalVo>> getBankBillTotalVoMap(List<BankBillTotalVo> list) {
        Map<Integer, Map<String, BankBillTotalVo>> map = new HashMap<>();
        if (null != list) {
            for (BankBillTotalVo vo : list) {
                // 时间维度初始化数据
                Map<String, BankBillTotalVo> decimals = map.get(vo.getTransactionType());
                if (null == decimals) {
                    decimals = new HashMap<>();
                    map.put(vo.getTransactionType(), decimals);
                }
                decimals.put(vo.getTime(), vo);
            }
        }
        return map;

    }
}
