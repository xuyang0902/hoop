package com.tongbanjie.hoop.tcc.response;

import java.io.Serializable;

/**
 * @author xu.qiang
 * @date 18/10/30
 */
public class BuyResponse implements Serializable {

    private static final long serialVersionUID = -2102924361394865526L;
    public static final Integer SUCC = 1;
    public static final Integer Failed = -1;
    public static final Integer UNKNOW = 0;
    public static final Integer Proccess = 2;

    /**
     * 预定状态
     */
    private Integer status;


    /**
     * 没有预定成功 提示信息
     */
    private String errorMsg;


    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    @Override
    public String toString() {
        return "BuyResponse{" +
                "status=" + status +
                ", errorMsg='" + errorMsg + '\'' +
                '}';
    }
}
