package com.tongbanjie.hoop.api.storage.plugins.repository;

import com.tongbanjie.hoop.api.config.HoopClientConfig;
import com.tongbanjie.hoop.api.model.HoopBranch;
import com.tongbanjie.hoop.api.model.HoopGlobal;

import java.util.List;

/**
 * 分布式事务记录仓库【全局事务，分支事务】
 *
 * @author xu.qiang
 * @date 18/8/15
 */
public interface TransactionRepositry {


    /**
     * 添加全局事务日志记录
     *
     * @param hoopGlobal
     */
    HoopGlobal addGlobalLog(HoopGlobal hoopGlobal);

    /**
     * 更新全局事务记录
     *
     * @param hoopGlobal
     * @return
     */
    void updateGlobalLog(HoopGlobal hoopGlobal);

    /**
     * 查询全局事务记录
     *
     * @param tsId
     * @return
     */
    HoopGlobal selectGlobalById(String tsId);

    /**
     * 移除global单据
     * @param tsId
     */
    void removeGlobal(String tsId);

    /**
     * 添加活动日志 【需要在一个事务中】
     *
     * @param hoopBranch
     * @param hoopGlobal
     */
    void addBranch(HoopBranch hoopBranch, HoopGlobal hoopGlobal);

    /**
     * 更新活动日志
     *
     * @param hoopBranch
     */
    void updateBranch(HoopBranch hoopBranch);

    /**
     * 根据tsId查询分支事务集合
     *
     * @param tsId
     * @return
     */
    List<HoopBranch> selectBranchByTsId(String tsId);

    /**
     * 移除所有事务记录  【需要在一个事务中】
     *
     * @param tsId
     */
    void removeTransaction(String tsId);


    /**
     * 获取需要恢复的活动日志
     * <p>
     * 1、获取事务类型：hoopType 对应的Hooplock
     * 2、查询beforTime分钟之前的global_log
     * 3、更新global_log的修改时间为当前时间
     * 4、返回查出来的这批global_log
     * <p>
     * ps：这样的话 这批global_log就可以去做恢复了，别的机器再抢到lock 拿到的global_log应该是另外一批数据
     * 但是不排除这批global_log处理的特别慢，等到下次恢复线程都查到同一批数据了。
     * <p>
     * 默认的恢复器 查询 X分钟前的单据，更新为now   从now开始 他只能处理X分钟，处理到X分钟的话，直接超时，下次处理
     * 因为X分钟后，如果这批数据处理的比较慢，别的机器线程可能会查询到这批数据。
     *
     * @param startTsId 起始tdIs
     * @param hoopType 事务类型
     * @param hoopClientConfig 基础配置
     * @return 全局事务列表
     */
    List<HoopGlobal> getNeedRecoverGlobalInfos(String startTsId, String hoopType, HoopClientConfig hoopClientConfig);


    /*  使用mysql作为存储引擎的时候，在事务恢复的时候需要引入lock处理并发问题 */

    /**
     * 初始化hoopLock
     *
     * @param appName
     */
    void initHoopLock(String appName);


}
