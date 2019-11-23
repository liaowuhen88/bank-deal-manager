package com.wwt.managemail.entity;

import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "bank_name")
@Data
public class BankName {
    @Id
    @KeySql(useGeneratedKeys = true)
    private Integer id;
    private String name;

}