package com.tongbanjie.hoop.api.recovery;

/**
 * 事务恢复执行器
 *
 * @author xu.qiang
 * @date 18/8/22
 */
public interface HoopRecover {


    /**
     * 处理某一种类型的事务单据
     *
     * @param hoopType
     */
    void handleHoopGlobals(String hoopType);

}
