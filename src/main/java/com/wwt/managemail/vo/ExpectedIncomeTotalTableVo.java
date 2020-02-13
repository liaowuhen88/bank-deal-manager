package com.wwt.managemail.vo;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

@Data
public class ExpectedIncomeTotalTableVo {
    @DateTimeFormat(pattern = "yyyy-MM")
    private String startTime;

    @DateTimeFormat(pattern = "yyyy-MM")
    private String endTime;
}
