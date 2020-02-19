package com.wwt.managemail.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class QueryByTimeVo {
    @NotNull
    private String time;
    @NotNull
    private int[] transactionTypes;
}
