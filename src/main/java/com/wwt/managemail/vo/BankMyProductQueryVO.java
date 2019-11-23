package com.wwt.managemail.vo;

import lombok.Data;

import java.util.Date;

@Data
public class BankMyProductQueryVO extends BankQueryVO {
    /**
     * 到期时间
     */
    private Date expireTime;
}
