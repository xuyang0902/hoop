package com.tongbanjie.hoop.storage.mysql.repository.jdbc.dao;

import com.tongbanjie.hoop.api.enums.HoopType;
import com.tongbanjie.hoop.api.model.HoopContext;
import com.tongbanjie.hoop.api.exception.HoopException;
import com.tongbanjie.hoop.api.model.HoopGlobal;
import com.tongbanjie.hoop.api.model.query.GlobalTransactionQuery;
import com.tongbanjie.hoop.api.storage.plugins.repository.GlobalTransactionDao;
import com.tongbanjie.hoop.storage.mysql.repository.jdbc.support.JdbcSupport;
import com.tongbanjie.hoop.storage.mysql.serializer.KryoPoolSerializer;
import com.tongbanjie.hoop.storage.mysql.serializer.ObjectSerializer;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author xu.qiang
 * @date 18/8/20
 */
public class GlobalTransactionDaoImpl extends JdbcSupport implements GlobalTransactionDao {

    private static final String INSERT = "INSERT INTO hoop_global_transaction (ts_id,ts_type,action_count,start_time,context,state,recover_count,timeout,resolver_bean,gmt_create,gmt_modify,env) VALUES (?,?,?,now(),?,?,?,?,?,now(),now(),?)";
    private static final String DELETE = "DELETE FROM hoop_global_transaction where ts_id = ?";
    private static final String SELECT = "SELECT * FROM hoop_global_transaction where ts_id = ? ";
    private static final String SELECT_FOR_UPDATE = "SELECT * FROM hoop_global_transaction where ts_id = ? for update";
    private static final String UPDATE_PREFIX = "UPDATE hoop_global_transaction SET ";
    private static final String WHERE_TS_ID = "where ts_id = ? ";
    private static final String UPDATE_MODIFY_TIME = "UPDATE hoop_global_transaction SET gmt_modify = ? where ts_id in (?)";

    /**
     * 查询非终态的活动日志
     */
    private static final String SELECT_ACTIVITY = "select * from hoop_global_transaction where 1 = 1 ";
    private static final String SELECT_ACTIVITY_COUNT = "select count(1) from hoop_global_transaction where 1 = 1 ";

    private static final String ts_id = "ts_id";
    private static final String ts_type = "ts_type";
    private static final String action_count = "action_count";
    private static final String start_time = "start_time";
    private static final String context = "context";
    private static final String state = "state";
    private static final String recover_count = "recover_count";
    private static final String timeout = "timeout";
    private static final String resolver_bean = "resolver_bean";
    private static final String gmt_create = "gmt_create";
    private static final String gmt_modify = "gmt_modify";
    private static final String env = "env";


    /**
     * 做事务恢复的时候，需要拿到对应的hoop的事务锁，才更操作activity数据。
     */
    private static final String SELECT_FROM_HOOP_LOCK_FOR_UPDATE = "select 1 from hoop_lock where app = ? and ts_type = ?  limit 1 for update";
    private static final String INSERT_HOOP_LOCK_ON_DUPLICATE_KEY = "insert into hoop_lock (app, ts_type, create_time, modify_time) values (?, ?, now(),now()) ON DUPLICATE KEY UPDATE modify_time = values(modify_time)";

    private ObjectSerializer<HoopContext> serializer = new KryoPoolSerializer<HoopContext>();

    private ObjectSerializer<Class> serializerCLass = new KryoPoolSerializer<Class>();


    @Override
    public void initHoopLock(String appName) {

        getJdbcTemplate().update(INSERT_HOOP_LOCK_ON_DUPLICATE_KEY, appName, HoopType.TCC.getCode());

        getJdbcTemplate().update(INSERT_HOOP_LOCK_ON_DUPLICATE_KEY, appName, HoopType.COMPENSATE.getCode());

    }

    @Override
    public void selectHoopLockForUpdate(String appName, String hoopType) {

        Integer num = getJdbcTemplate().queryForObject(SELECT_FROM_HOOP_LOCK_FOR_UPDATE, new Object[]{appName, hoopType}, new RowMapper<Integer>() {
            @Override
            public Integer mapRow(ResultSet resultSet, int i) throws SQLException {

                return resultSet.getInt(1);
            }
        });

        if (num == null || num != 1) {
            throw new HoopException("获取hoop_lock未查询到相关的事务记录lock，App:" + appName + " hoopType:" + hoopType);
        }
    }

    /****************************************************************/

    @Override
    public boolean insert(HoopGlobal hoopGlobal) {
        return 1 == getJdbcTemplate().update(INSERT,
                hoopGlobal.getTsId(),
                hoopGlobal.getTsType(),
                hoopGlobal.getActionCount(),
                serializer.serialize(hoopGlobal.getContext()),
                hoopGlobal.getState(),
                hoopGlobal.getRecoverCount(),
                hoopGlobal.getTimeout(),
                serializerCLass.serialize(hoopGlobal.getResolverBean()),
                hoopGlobal.getEnv());
    }

    @Override
    public boolean deleteByTsId(String tsId) {
        return 1 == getJdbcTemplate().update(DELETE, tsId);
    }

    @Override
    public boolean updateByTsId(HoopGlobal hoopGlobal) {

        StringBuilder updateSql = new StringBuilder(UPDATE_PREFIX);

        List<Object> objects = new ArrayList<Object>();//

        Integer recoverCount = hoopGlobal.getRecoverCount();
        if (recoverCount != null) {
            updateSql.append(recover_count).append(" = ? ,");

            objects.add(recoverCount);
        }
        Integer actionCount = hoopGlobal.getActionCount();
        if (actionCount != null) {
            updateSql.append(action_count).append(" = ? ,");
            objects.add(actionCount);
        }
        String state = hoopGlobal.getState();
        if (state != null) {
            updateSql.append(GlobalTransactionDaoImpl.state).append(" = ? ,");
            objects.add(state);
        }

        updateSql.append(gmt_modify).append("=now() ");
        updateSql.append(WHERE_TS_ID);
        objects.add(hoopGlobal.getTsId());

        return 1 == getJdbcTemplate().update(updateSql.toString(), objects.toArray());
    }

    @Override
    public boolean updateByTsIds(String ids, Date now) {
        return 1 == getJdbcTemplate().update(UPDATE_MODIFY_TIME, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(now), ids);
    }

    @Override
    public HoopGlobal selectByTsId(String tsId) {
        List<HoopGlobal> list = getJdbcTemplate().query(SELECT, new Object[]{tsId}, new ActivityRowMapper());
        if (!CollectionUtils.isEmpty(list)) {
            return list.get(0);
        }

        return null;
    }

    @Override
    public HoopGlobal selectByTsIdForUpdate(String tsId) {
        return getJdbcTemplate().queryForObject(SELECT_FOR_UPDATE, new Object[]{tsId}, new ActivityRowMapper());
    }

    @Override
    public List<HoopGlobal> query(GlobalTransactionQuery globalTransactionQuery) {

        StringBuilder sb = new StringBuilder(SELECT_ACTIVITY);

        sb.append(whereSql(globalTransactionQuery));

        Integer offset = globalTransactionQuery.getOffset();
        Integer limit = globalTransactionQuery.getLimit();
        sb.append(" order by ts_id asc limit ").append(offset).append(",").append(limit);

        return getJdbcTemplate().query(sb.toString(), new ActivityRowMapper());
    }

    @Override
    public Integer count(GlobalTransactionQuery globalTransactionQuery) {
        StringBuilder sb = new StringBuilder(SELECT_ACTIVITY_COUNT);

        sb.append(whereSql(globalTransactionQuery));

        return getJdbcTemplate().queryForObject(sb.toString(), Integer.class);
    }

    private String whereSql(GlobalTransactionQuery globalTransactionQuery) {

        StringBuilder sb = new StringBuilder("");
        String tsId = globalTransactionQuery.getTsId();
        if (!StringUtils.isEmpty(tsId)) {
            sb.append(" and ").append(ts_id).append(" = '").append(tsId).append("'");
        }

        String tsType = globalTransactionQuery.getTsType();
        if (!StringUtils.isEmpty(tsType)) {
            sb.append(" and ").append(ts_type).append(" = '").append(tsType).append("'");
        }

        Integer eltRecoverCount = globalTransactionQuery.getEltRecoverCount();
        if (eltRecoverCount != null) {
            sb.append(" and ").append(recover_count).append(" <= ").append(eltRecoverCount);
        }

        String stateList = globalTransactionQuery.getStateListForString();
        if (!StringUtils.isEmpty(stateList)) {
            sb.append(" and ").append(state).append(" in (").append(stateList).append(")");
        }

        String startTsId = globalTransactionQuery.getStartTsId();
        if (!StringUtils.isEmpty(startTsId)) {
            sb.append(" and ").append(ts_id).append(" > '").append(startTsId).append("'");
        }

        String eltModifyTime = globalTransactionQuery.getEltModifyTime();
        if (!StringUtils.isEmpty(eltModifyTime)) {
            sb.append(" and ").append(gmt_modify).append(" < '").append(eltModifyTime).append("'");
        }

        return sb.toString();
    }

    class ActivityRowMapper implements RowMapper<HoopGlobal> {

        @Override
        public HoopGlobal mapRow(ResultSet rs, int i) throws SQLException {
            HoopGlobal hoopGlobal = new HoopGlobal();
            hoopGlobal.setTsId(rs.getString(ts_id));
            hoopGlobal.setTsType(rs.getString(ts_type));
            hoopGlobal.setActionCount(rs.getInt(action_count));
            Timestamp startTime = rs.getTimestamp(start_time);
            if (startTime != null) {
                hoopGlobal.setStartTime(new Date(startTime.getTime()));
            }
            hoopGlobal.setContext(serializer.deserialize(rs.getBytes(context)));
            hoopGlobal.setState(rs.getString(state));
            hoopGlobal.setRecoverCount(rs.getInt(recover_count));
            hoopGlobal.setTimeout(rs.getInt(timeout));
            hoopGlobal.setResolverBean(serializerCLass.deserialize(rs.getBytes(resolver_bean)));

            Timestamp createTime = rs.getTimestamp(gmt_create);
            if (createTime != null) {
                hoopGlobal.setGmtCreate(new Date(createTime.getTime()));
            }
            Timestamp modifyTime = rs.getTimestamp(gmt_modify);
            if (modifyTime != null) {
                hoopGlobal.setGmtModify(new Date(modifyTime.getTime()));
            }
            hoopGlobal.setEnv(rs.getString(env));

            return hoopGlobal;
        }
    }

}
