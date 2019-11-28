package com.wwt.managemail.service.impl;

import com.alibaba.fastjson.JSON;
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
        insertSelective(bankBill);
        bankService.transaction(bankBill);
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
    public List<BankBillVo> query(BankBillQuery bankBillQuery) {
        return bankBillMapper.query(bankBillQuery);
    }

    @Override
    public List<BankBillTotalVo> queryCurrentYearTotal() {
        BankBillQuery bankBillQuery = new BankBillQuery();
        bankBillQuery.setStartTime(TimeUtils.getCurrentYeadFirstDay());
        bankBillQuery.setEndTime(new Date());
        return bankBillMapper.queryTotal(bankBillQuery);
    }

    @Override
    public StackedLineChart totalByMonth(BankBillQuery bankBillQuery) throws Exception {
        bankBillQuery.setStartTime(TimeUtils.getCurrentYeadFirstDay());
        bankBillQuery.setEndTime(new Date());
        //创建图表数据
        StackedLineChart stackedLineChart = new StackedLineChart();
        // 生成横轴数据
        List<String> times = TimeUtils.getMonthBetween(bankBillQuery.getStartTime(), bankBillQuery.getEndTime());
        Map<Integer, Serie> mapSeries = initSeries(times);
        XAxis xAxis = new XAxis();
        xAxis.setData(times);
        stackedLineChart.setXAxis(xAxis);
        stackedLineChart.setSeries(new ArrayList<>(mapSeries.values()));

        List<BankBillTotalVo> list = bankBillMapper.totalByMonth(bankBillQuery);
        logger.info(JSON.toJSONString(list));
        Map<Integer, Map<String, BankBillTotalVo>> map = getBankBillTotalVoMap(list);
        logger.info(JSON.toJSONString(map));
        if (null != mapSeries) {
            Iterator<Map.Entry<Integer, Serie>> iterator = mapSeries.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Integer, Serie> entry = iterator.next();
                Integer key = entry.getKey();
                Map<String, BankBillTotalVo> bankBillTotalVoMap = map.get(key);
                Serie serie = entry.getValue();
                List<String> date = serie.getData();

                for (String time : times) {
                    if (null == bankBillTotalVoMap) {
                        date.add("");
                    } else {
                        BankBillTotalVo bankBillTotalVo = bankBillTotalVoMap.get(time);
                        if (null == bankBillTotalVo) {
                            date.add("");
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

    public Map<Integer, Serie> initSeries(List<String> times) {
        Map<Integer, Serie> map = new HashMap<>();

        List<String> investmentIncomeData = new ArrayList<>(times.size());
        Serie investmentIncome = new Serie();
        investmentIncome.setName(TransactionTypeEnum.investmentIncome.getMsg());
        investmentIncome.setData(investmentIncomeData);
        map.put(TransactionTypeEnum.investmentIncome.getCode(), investmentIncome);

        Serie pay = new Serie();
        List<String> payData = new ArrayList<>(times.size());
        pay.setName(TransactionTypeEnum.pay.getMsg());
        pay.setData(payData);
        map.put(TransactionTypeEnum.pay.getCode(), pay);

        return map;

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
