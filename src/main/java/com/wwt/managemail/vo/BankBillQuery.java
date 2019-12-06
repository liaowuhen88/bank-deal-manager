package com.wwt.managemail.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class BankBillQuery {
    private String userName;
    private String bankName;
    private String bankCard;
    private int[] transactionTypes;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date[] times;

    public Date getStartTime() {
        if (null != times && times.length == 2) {
            return times[0];
        }
        return startTime;
    }

    public Date getEndTime() {
        if (null != times && times.length == 2) {
            return times[1];
        }
        return endTime;
    }
    private Date startTime;
    private Date endTime;
}
