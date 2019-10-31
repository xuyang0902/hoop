package com.tongbanjie.hoop.api.enums;

/**
 * 分布式事务类型
 *
 * @author xu.qiang
 * @date 18/8/30
 */
public enum HoopType {

    TCC("TCC", "tcc事务"),
    COMPENSATE("DO", "补偿事务");

    private String code;

    private String message;

    HoopType(String code, String message) {
        this.code = code;
        this.message = message;
    }


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
