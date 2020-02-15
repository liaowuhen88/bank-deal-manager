package com.wwt.managemail.controller;

import com.github.pagehelper.Page;
import com.wwt.managemail.common.Result;
import com.wwt.managemail.entity.BankBill;
import com.wwt.managemail.service.BankBillService;
import com.wwt.managemail.vo.BankBillQuery;
import com.wwt.managemail.vo.BankBillVo;
import com.wwt.managemail.vo.StackedLineChart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/bankBill")
public class BankBillController extends BaseController {

    @Autowired
    BankBillService billService;

    @PostMapping("transaction")
    public Result<Integer> transaction(@Validated @RequestBody BankBill bankBill){
        int code = billService.transaction(bankBill);
        return Result.sucess(code);
    }

    @PostMapping("query")
    public Result<List<BankBillVo>> query(@Validated @RequestBody BankBillQuery bankBillQuery) {
        Page<BankBillVo> list = billService.query(bankBillQuery);
        Result result = Result.sucess(list);
        result.setTotal(list.getTotal());
        return result;
    }

    @PostMapping("queryLaste")
    public Result<List<BankBillVo>> queryLaste(@Validated @RequestBody BankBillQuery bankBillQuery) {
        BankBillVo list = billService.queryLaste(bankBillQuery);
        Result result = Result.sucess(list);
        return result;
    }

    @PostMapping("totalByMonth")
    public Result<StackedLineChart> totalByMonth(@Validated @RequestBody BankBillQuery bankBillQuery) throws Exception {
        StackedLineChart list = billService.totalByMonthEchart(bankBillQuery);
        return Result.sucess(list);
    }
}
