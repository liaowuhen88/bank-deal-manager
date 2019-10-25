package com.wwt.managemail.common;

public enum Code {

    SUCCESS(0, "成功"),
    PERMISSION_DENIED(1001, "权限受限!"),
    SYS_ERR(1002, "系统错误!"),
    ;

    private final int code;

    private final String message;

    Code(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
