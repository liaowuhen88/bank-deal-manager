package com.wwt.managemail.enums;

public enum TransactionTypeEnum {
    income(1, "转入"),
    pay(2, "支出"),
    transfer_in(7, "转账-转入"),
    transfer_out(3, "转账-转出"),
    cashInterestIncome(4, "活期利息收入"),
    investment(5, "买入理财"),
    investmentIncome(6, "理财利息收入"),
    ;
    private int code;

    private String msg;

    TransactionTypeEnum(int code, String msg) {
        this.msg = msg;
        this.code = code;
    }

    public static TransactionTypeEnum getByCode(int code) {
        for (TransactionTypeEnum e : TransactionTypeEnum.values()) {
            if (code == e.getCode()) {
                return e;
            }
        }
        throw new RuntimeException(code + "交易不存在");
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

}
