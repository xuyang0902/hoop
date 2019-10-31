package com.tongbanjie.hoop.storage.mysql.repository;

import com.tongbanjie.hoop.api.config.HoopClientConfig;
import com.tongbanjie.hoop.api.enums.GlobalState;
import com.tongbanjie.hoop.api.model.HoopBranch;
import com.tongbanjie.hoop.api.model.HoopGlobal;
import com.tongbanjie.hoop.api.model.query.GlobalTransactionQuery;
import com.tongbanjie.hoop.api.storage.plugins.repository.BranchTransactionDao;
import com.tongbanjie.hoop.api.storage.plugins.repository.GlobalTransactionDao;
import com.tongbanjie.hoop.api.storage.plugins.repository.TransactionRepositry;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author xu.qiang
 * @date 18/8/20
 */
public class JdbcTransactionReposity implements TransactionRepositry {

    private GlobalTransactionDao globalTransactionDao;

    private BranchTransactionDao branchTransactionDao;

    private HoopClientConfig hoopClientConfig;

    /**
     * 添加活动日志
     *
     * @param hoopGlobal
     */
    @Override
    @Transactional(rollbackFor = Throwable.class, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED)
    public HoopGlobal addGlobalLog(HoopGlobal hoopGlobal) {

        Assert.notNull(hoopGlobal.getTsId(), "开启主事务时 tsId不能为空");

        Date now = new Date();
        hoopGlobal.setActionCount(0);
        hoopGlobal.setGmtCreate(now);
        hoopGlobal.setRecoverCount(0);
        hoopGlobal.setStartTime(now);
        hoopGlobal.setState(GlobalState.INIT.getCode());

        if (!globalTransactionDao.insert(hoopGlobal)) {
            throw new RuntimeException("开启主事务异常");
        }

        return hoopGlobal;

    }

    @Override
    public void updateGlobalLog(HoopGlobal hoopGlobal) {

        if (!globalTransactionDao.updateByTsId(hoopGlobal)) {
            throw new RuntimeException("更新主事务日志异常");
        }

    }

    @Override
    public HoopGlobal selectGlobalById(String tsId) {
        return globalTransactionDao.selectByTsId(tsId);
    }

    @Override
    public void removeGlobal(String tsId) {

        //生产刚跑的业务 日志先不删除
        if (!hoopClientConfig.isRemoveLog()) {
            return;
        }

        globalTransactionDao.deleteByTsId(tsId);
    }

    /**
     * 添加活动日志
     *
     * @param hoopBranch
     */
    @Override
    @Transactional(rollbackFor = Throwable.class, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED)
    public void addBranch(HoopBranch hoopBranch, HoopGlobal hoopGlobal) {

        if (!branchTransactionDao.insert(hoopBranch)) {
            throw new RuntimeException("添加branch事务异常");
        }


        hoopGlobal.setActionCount(hoopGlobal.getActionCount() + 1);
        updateGlobalLog(hoopGlobal);
    }

    @Override
    public void updateBranch(HoopBranch hoopBranch) {

        if (!branchTransactionDao.updateByActionId(hoopBranch)) {
            throw new RuntimeException("更新branch事务异常");
        }

    }

    @Override
    public List<HoopBranch> selectBranchByTsId(String tsId) {
        return branchTransactionDao.select(tsId);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED)
    public void removeTransaction(String tsId) {

        //根据配置来
        if (!hoopClientConfig.isRemoveLog()) {
            return;
        }

        globalTransactionDao.deleteByTsId(tsId);
        branchTransactionDao.delete(tsId);

    }

    @Override
    @Transactional(rollbackFor = Throwable.class, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED)
    public List<HoopGlobal> getNeedRecoverGlobalInfos(String startTsId, String hoopType, HoopClientConfig hoopClientConfig) {

        //获取事务锁
        globalTransactionDao.selectHoopLockForUpdate(hoopClientConfig.getAppName(), hoopType);

        long l = hoopClientConfig.getBeforTime() * 1000L * 60;
        String eltModifyTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis() - l));

        //查询x时间前的事务单据
        GlobalTransactionQuery activityQuery = new GlobalTransactionQuery();
        activityQuery.setStartTsId(startTsId);
        activityQuery.setTsType(hoopType);
        activityQuery.setEltModifyTime(eltModifyTime);
        List<String> stateList = new ArrayList<String>();
        stateList.add(GlobalState.INIT.getCode());
        stateList.add(GlobalState.UNKOWN.getCode());
        stateList.add(GlobalState.COMMIT.getCode());
        stateList.add(GlobalState.ROLLBACK.getCode());
        //stateList.add(GlobalState.FINISH.getCode());
        activityQuery.setStateList(stateList);
        activityQuery.setEltRecoverCount(hoopClientConfig.getMaxRecoverCount());
        activityQuery.setOffset(0);
        activityQuery.setLimit(100);
        List<HoopGlobal> list = globalTransactionDao.query(activityQuery);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        Date now = new Date();
        for (HoopGlobal hoopGlobal : list) {
            sb.append("'").append(hoopGlobal.getTsId()).append("',");
            hoopGlobal.setGmtModify(now);
        }
        int i = sb.lastIndexOf(",");

        //更新这批事务单据的修改时间为当前时间

        globalTransactionDao.updateByTsIds(sb.substring(0, i), now);

        return list;
    }

    @Override
    public void initHoopLock(String appName) {
        globalTransactionDao.initHoopLock(appName);
    }


    public GlobalTransactionDao getGlobalTransactionDao() {
        return globalTransactionDao;
    }

    public void setGlobalTransactionDao(GlobalTransactionDao globalTransactionDao) {
        this.globalTransactionDao = globalTransactionDao;
    }

    public BranchTransactionDao getBranchTransactionDao() {
        return branchTransactionDao;
    }

    public void setBranchTransactionDao(BranchTransactionDao branchTransactionDao) {
        this.branchTransactionDao = branchTransactionDao;
    }

    public HoopClientConfig getHoopClientConfig() {
        return hoopClientConfig;
    }

    public void setHoopClientConfig(HoopClientConfig hoopClientConfig) {
        this.hoopClientConfig = hoopClientConfig;
    }
}
