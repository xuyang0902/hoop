package com.tongbanjie.hoop.api.model.query;

import com.tongbanjie.hoop.api.enums.GlobalState;
import com.tongbanjie.hoop.api.enums.HoopType;

import java.io.Serializable;
import java.util.List;

/**
 * 分布式事务活动开始日志
 *
 * @author xu.qiang
 * @date 18/8/7
 */
public class GlobalTransactionQuery implements Serializable {

    private static final long serialVersionUID = 808686556413137077L;


    /**
     * 其实的tsId
     */
    private String startTsId;

    /**
     * 修改时间 <= eltModifyTime
     */
    private String eltModifyTime;

    /**
     * 事务ID
     */
    private String tsId;

    /**
     * 事务类型
     *
     * @see HoopType
     */
    private String tsType;

    /**
     * U:未知,I:初始,C:提交中,R:回滚中,F:完成
     *
     * @see GlobalState
     */
    private List<String> stateList;

    /**
     * 恢复次数 <= eltRecoverCount
     */
    private Integer eltRecoverCount;

    /**
     * 偏移量
     */
    private Integer offset = 0;

    /**
     * 一次sql查询数量
     */
    private Integer limit = 25;

    public String getTsId() {
        return tsId;
    }

    public void setTsId(String tsId) {
        this.tsId = tsId;
    }

    public String getTsType() {
        return tsType;
    }

    public void setTsType(String tsType) {
        this.tsType = tsType;
    }

    public List<String> getStateList() {
        return stateList;
    }

    public void setStateList(List<String> stateList) {
        this.stateList = stateList;
    }

    public Integer getEltRecoverCount() {
        return eltRecoverCount;
    }

    public void setEltRecoverCount(Integer eltRecoverCount) {
        this.eltRecoverCount = eltRecoverCount;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public String getStartTsId() {
        return startTsId;
    }

    public void setStartTsId(String startTsId) {
        this.startTsId = startTsId;
    }

    public String getEltModifyTime() {
        return eltModifyTime;
    }

    public void setEltModifyTime(String eltModifyTime) {
        this.eltModifyTime = eltModifyTime;
    }


    public String getStateListForString() {
        if (stateList == null || stateList.size() == 0) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (String s : stateList) {
            sb.append("'").append(s).append("',");
        }

        int i = sb.lastIndexOf(",");

        return sb.substring(0, i).toString();
    }
}
