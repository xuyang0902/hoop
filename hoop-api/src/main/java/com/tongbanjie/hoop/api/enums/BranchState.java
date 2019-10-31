package com.tongbanjie.hoop.api.enums;

/**
 * action状态
 *
 * @author xu.qiang
 * @date 18/8/13
 */
public enum BranchState {

    /*初始*/
    INIT("I", "初始"),
    /*已完成 */
    FINISH("F", "已完成");

    private String code;

    private String message;

    BranchState(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
