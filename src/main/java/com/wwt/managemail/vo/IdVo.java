package com.wwt.managemail.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class IdVo {
    @NotNull
    private Integer id;
}
