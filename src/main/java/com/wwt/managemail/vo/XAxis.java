package com.wwt.managemail.vo;

import lombok.Data;

import java.util.List;

@Data
public class XAxis {
    private String type = "category";
    private boolean boundaryGap = false;
    private List<String> data;

}
