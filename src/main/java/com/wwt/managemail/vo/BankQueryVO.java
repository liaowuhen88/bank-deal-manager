package com.wwt.managemail.vo;

import lombok.Data;

@Data
public class BankQueryVO extends CommonSearchVO {
    private String bankName;
    private String userName;
}
