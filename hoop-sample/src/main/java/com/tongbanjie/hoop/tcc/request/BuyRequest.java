package com.tongbanjie.hoop.tcc.request;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author xu.qiang
 * @date 18/10/30
 */
public class BuyRequest implements Serializable {

    /**
     * 出发地
     */
    private String fromCity;

    /**
     * 目标城市
     */
    private String toCity;

    /**
     * 用户id
     */
    private String tbjUserId;

    /**
     * 该次订单本地单号
     */
    private String orderNo;

    /**
     * 总金额
     */
    private BigDecimal totalAmount;


    public String getFromCity() {
        return fromCity;
    }

    public void setFromCity(String fromCity) {
        this.fromCity = fromCity;
    }

    public String getToCity() {
        return toCity;
    }

    public void setToCity(String toCity) {
        this.toCity = toCity;
    }

    public String getTbjUserId() {
        return tbjUserId;
    }

    public void setTbjUserId(String tbjUserId) {
        this.tbjUserId = tbjUserId;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }


    @Override
    public String toString() {
        return "BuyRequest{" +
                "fromCity='" + fromCity + '\'' +
                ", toCity='" + toCity + '\'' +
                ", tbjUserId='" + tbjUserId + '\'' +
                ", orderNo='" + orderNo + '\'' +
                ", totalAmount=" + totalAmount +
                '}';
    }
}
