package com.wwt.managemail.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class CommonSearchVO implements Serializable {

    private Integer page;

    private Integer pageSize;

    private String queryKey;

}
