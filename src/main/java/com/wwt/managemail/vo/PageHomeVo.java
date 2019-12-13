package com.wwt.managemail.vo;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class PageHomeVo {
    BankTotalVo bankTotalVo;
    Map<String, BankBillTotalVo> integerBankBillTotalVoMap;
    /**
     * 到期产品
     */
    List<BankMyProductVo> expireProduct;
    /**
     * 到期利息
     */
    List<BankMyProductVo> expireInterest;
}
