package com.tongbanjie.hoop.api.storage.plugins.repository;


import com.tongbanjie.hoop.api.model.HoopBranch;

import java.util.List;

/**
 * 事务参与原子服务
 *
 * @author xu.qiang
 * @date 18/7/14
 */
public interface BranchTransactionDao {

    /**
     * 新增活动日志
     *
     * @param action
     * @return
     */
    boolean insert(HoopBranch action);

    /**
     * 删除活动日志
     *
     * @param tsId
     * @return
     */
    boolean delete(String tsId);

    /**
     * 修改活动日志
     *
     * @param action  actionId必传。否则无法修改
     * @return
     */
    boolean updateByActionId(HoopBranch action);

    /**
     * 查询活动日志
     *
     * @param tsId
     * @return
     */
    List<HoopBranch> select(String tsId);


}
