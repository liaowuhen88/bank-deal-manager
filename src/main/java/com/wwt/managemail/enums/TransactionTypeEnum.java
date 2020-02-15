package com.wwt.managemail.enums;

public enum TransactionTypeEnum {
    income(1, "income", "转入"),
    pay(2, "pay", "支出"),
    transfer_in(7, "transfer_in", "转账-转入"),
    transfer_out(3, "transfer_out", "转账-转出"),
    cashInterestIncome(4, "cashInterestIncome", "活期利息收入"),
    investment(5, "investment", "买入理财"),
    investmentIncome(6, "investmentIncome", "理财利息收入"),
    init_new(8, "init_new", "初始建账"),
    investment_redeem(9, "investment_redeem", "理财赎回"),
    investment_redeem_principal(11, "investment_redeem_principal", "理财赎回本金"),
    ;
    private int code;

    private String enCode;

    private String msg;

    TransactionTypeEnum(int code, String enCode, String msg) {
        this.msg = msg;
        this.enCode = enCode;
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

    public String getEnCode() {
        return enCode;
    }

    public String getMsg() {
        return msg;
    }

}
