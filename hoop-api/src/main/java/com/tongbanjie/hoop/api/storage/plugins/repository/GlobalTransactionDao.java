package com.tongbanjie.hoop.api.storage.plugins.repository;

import com.tongbanjie.hoop.api.model.HoopGlobal;
import com.tongbanjie.hoop.api.model.query.GlobalTransactionQuery;

import java.util.Date;
import java.util.List;

/**
 * 活动日志CRUD
 *
 * @author xu.qiang
 * @date 18/7/14
 */
public interface GlobalTransactionDao {

    /**
     * 初始化hoopLock
     *
     * @param appName
     */
    void initHoopLock(String appName);

    /**
     * 获取hoop的锁
     *
     * @param appName
     * @param hoopType
     * @return
     */
    void selectHoopLockForUpdate(String appName, String hoopType);


    /****************************************************************/

    /**
     * 新增活动日志
     *
     * @param hoopGlobal
     * @return
     */
    boolean insert(HoopGlobal hoopGlobal);

    /**
     * 删除活动日志
     *
     * @param tsId
     * @return
     */
    boolean deleteByTsId(String tsId);

    /**
     * 修改活动日志
     *
     * @param hoopGlobal
     * @return
     */
    boolean updateByTsId(HoopGlobal hoopGlobal);

    /**
     * 更新活动日志的修改时间
     *
     * @param ids
     * @return
     */
    boolean updateByTsIds(String ids, Date now);

    /**
     * 查询活动日志
     *
     * @param tsId
     * @return
     */
    HoopGlobal selectByTsId(String tsId);

    /**
     * 查询活动日志 并 for update 【db事务才生效】
     *
     * @param tsId
     * @return
     */
    HoopGlobal selectByTsIdForUpdate(String tsId);

    /**
     * 查询hoopGlobal
     *
     * @param globalTransactionQuery
     * @return
     */
    List<HoopGlobal> query(GlobalTransactionQuery globalTransactionQuery);

    /**
     * 计数
     *
     * @param globalTransactionQuery
     * @return
     */
    Integer count(GlobalTransactionQuery globalTransactionQuery);
}
