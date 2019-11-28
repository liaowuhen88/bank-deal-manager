package com.wwt.managemail.vo;

import lombok.Data;

import java.util.List;

@Data
public class Serie {
    private String name;
    private String type = "line";
    private String stack = "总量";
    private List<String> data;

}
