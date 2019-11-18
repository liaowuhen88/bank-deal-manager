package com.wwt.managemail.utils;

import com.wwt.managemail.enums.TransactionTypeEnum;

import java.math.BigDecimal;

public class MoneyUtills {
    public static BigDecimal getRealMoney(int code, BigDecimal money) {
        if (TransactionTypeEnum.pay.getCode() == code || TransactionTypeEnum.transfer.getCode() == code || TransactionTypeEnum.investment.getCode() == code) {
            return money.negate();
        }
        return money;
    }
}
