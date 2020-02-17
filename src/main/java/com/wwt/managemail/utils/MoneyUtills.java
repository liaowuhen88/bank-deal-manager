package com.wwt.managemail.utils;

import com.wwt.managemail.enums.TransactionTypeEnum;

import java.math.BigDecimal;

public class MoneyUtills {
    public static BigDecimal getRealMoney(int code, BigDecimal money) {
        if (TransactionTypeEnum.pay.getCode() == code || TransactionTypeEnum.transfer_out.getCode() == code || TransactionTypeEnum.investment.getCode() == code) {
            return money.negate();
        }
        return money;
    }

    public static String toMoney(BigDecimal num) {
        String[] dw2 = new String[]{"", "", "", "", "千", "万", "十万", "百万", "千万", "亿", "十亿", "百亿", "千亿"};// 大单位
        long source = num.longValue();
        //console.log(Math.abs(num).toString(), source[0],source[0].length);
        int len = String.valueOf(source).length();// 整数的长度
        String result = num.toString();
        if (len > 3) {
            result += '(' + dw2[len] + ')';
        }
        return result; // 返回的是字符串23,245.12保留2位小数
    }

    public static void main(String[] args) {
        System.out.println(toMoney(new BigDecimal(2600000)));
        System.out.println(toMoney(new BigDecimal("2600000.12")));
    }
}
