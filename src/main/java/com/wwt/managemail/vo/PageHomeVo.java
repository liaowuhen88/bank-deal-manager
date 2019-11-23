package com.wwt.managemail.vo;

import com.wwt.managemail.entity.BankMyProduct;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class PageHomeVo {
    BankTotalVo bankTotalVo;
    Map<Integer, BankBillTotalVo> integerBankBillTotalVoMap;
    List<BankMyProduct> expireProduct;

}
