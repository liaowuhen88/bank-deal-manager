package com.wwt.managemail.vo;

import lombok.Data;

import java.util.List;

@Data
public class Serie {
    private Integer key;
    private String name;
    private String type = "line";
    private String stack;
    private List<String> data;

}
