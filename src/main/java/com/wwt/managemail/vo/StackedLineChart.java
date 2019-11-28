package com.wwt.managemail.vo;

import lombok.Data;

import java.util.List;

@Data
public class StackedLineChart {
    private XAxis xAxis;
    private List<Serie> series;
}
