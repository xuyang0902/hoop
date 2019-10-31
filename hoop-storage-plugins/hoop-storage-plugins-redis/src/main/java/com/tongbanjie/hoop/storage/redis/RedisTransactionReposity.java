package com.tongbanjie.hoop.storage.redis;

import com.tongbanjie.hoop.api.config.HoopClientConfig;
import com.tongbanjie.hoop.api.model.HoopBranch;
import com.tongbanjie.hoop.api.model.HoopGlobal;
import com.tongbanjie.hoop.api.storage.plugins.repository.TransactionRepositry;

import java.util.List;

/**
 *
 *
 * todo...  redis的引擎 待生成
 * @author xu.qiang
 * @date 18/11/9
 */
public class RedisTransactionReposity implements TransactionRepositry {
    @Override
    public HoopGlobal addGlobalLog(HoopGlobal hoopGlobal) {
        return null;
    }

    @Override
    public void updateGlobalLog(HoopGlobal hoopGlobal) {

    }

    @Override
    public HoopGlobal selectGlobalById(String tsId) {
        return null;
    }

    @Override
    public void removeGlobal(String tsId) {

    }

    @Override
    public void addBranch(HoopBranch hoopBranch, HoopGlobal hoopGlobal) {

    }

    @Override
    public void updateBranch(HoopBranch hoopBranch) {

    }

    @Override
    public List<HoopBranch> selectBranchByTsId(String tsId) {
        return null;
    }

    @Override
    public void removeTransaction(String tsId) {

    }

    @Override
    public List<HoopGlobal> getNeedRecoverGlobalInfos(String startTsId, String hoopType, HoopClientConfig hoopClientConfig) {
        return null;
    }

    @Override
    public void initHoopLock(String appName) {

    }
}
