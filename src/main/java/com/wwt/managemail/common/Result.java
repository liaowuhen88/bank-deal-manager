package com.wwt.managemail.common;

import lombok.Data;

import java.util.List;

@Data
public class Result<T> {
    private int code;
    private T data;// 当前页数据
    private String msg;
    public Result() {
    }
    public Result(Code code) {
        this.code = code.getCode();
        this.msg = code.getMessage();
    }

    public Result(Code code, T data) {
        this.code = code.getCode();
        this.msg = code.getMessage();
        this.data = data;
    }

    public static Result sucess(Object data) {
        Result response = new Result(Code.SUCCESS,data);
        return response;
    }

    public static Result error(String msg) {
        Result response = new Result(Code.SYS_ERR);
        response.setMsg(msg);
        return response;
    }
}
