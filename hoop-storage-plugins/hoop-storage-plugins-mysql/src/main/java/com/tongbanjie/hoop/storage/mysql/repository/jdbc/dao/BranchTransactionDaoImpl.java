package com.tongbanjie.hoop.storage.mysql.repository.jdbc.dao;

import com.tongbanjie.hoop.api.model.HoopBranch;
import com.tongbanjie.hoop.api.model.HoopContext;
import com.tongbanjie.hoop.api.storage.plugins.repository.BranchTransactionDao;
import com.tongbanjie.hoop.storage.mysql.repository.jdbc.support.JdbcSupport;
import com.tongbanjie.hoop.storage.mysql.serializer.KryoPoolSerializer;
import com.tongbanjie.hoop.storage.mysql.serializer.ObjectSerializer;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author xu.qiang
 * @date 18/8/20
 */
public class BranchTransactionDaoImpl extends JdbcSupport implements BranchTransactionDao {

    private static final String INSERT = "INSERT INTO hoop_branch_transaction(branch_id,ts_id,action_name,state,context,gmt_create) VALUES(?,?,?,?,?,now())";
    private static final String UPDATE_PREFIX = "UPDATE hoop_branch_transaction set ";
    private static final String DELETE = "DELETE FROM hoop_branch_transaction where ts_id = ?";
    private static final String SELECT = "SELECT * FROM hoop_branch_transaction where ts_id = ?";

    private static final String branch_id = "branch_id";
    private static final String ts_id = "ts_id";
    private static final String action_name = "action_name";
    private static final String state = "state";
    private static final String context = "context";
    private static final String gmt_create = "gmt_create";
    private static final String gmt_modify = "gmt_modify";

    private static final String WHERE_BRANCH_ID = " where branch_id = ? ";

    private ObjectSerializer<HoopContext> serializer = new KryoPoolSerializer<HoopContext>();

    @Override
    public boolean insert(HoopBranch action) {

        return 1 == getJdbcTemplate().update(INSERT,
                action.getBranchId(),
                action.getTsId(),
                action.getActionName(),
                action.getState(),
                serializer.serialize(action.getContext()));
    }

    @Override
    public boolean delete(String tsId) {
        return 1 == getJdbcTemplate().update(DELETE, tsId);
    }

    @Override
    public boolean updateByActionId(HoopBranch action) {

        StringBuilder updateSql = new StringBuilder(UPDATE_PREFIX);

        List<Object> objects = new ArrayList<Object>();
        String state = action.getState();
        if (state != null) {
            updateSql.append(BranchTransactionDaoImpl.state).append(" = ? ,");
            objects.add(state);
        }

        updateSql.append(gmt_modify).append(" = now() ");

        updateSql.append(WHERE_BRANCH_ID);
        objects.add(action.getBranchId());

        return 1 == getJdbcTemplate().update(updateSql.toString(), objects.toArray());
    }

    @Override
    public List<HoopBranch> select(final String tsId) {

        return getJdbcTemplate().query(SELECT, new Object[]{tsId}, new RowMapper<HoopBranch>() {
            @Override
            public HoopBranch mapRow(ResultSet rs, int rowNum) throws SQLException {
                HoopBranch action = new HoopBranch();
                action.setBranchId(rs.getString(branch_id));
                action.setTsId(rs.getString(ts_id));
                action.setActionName(rs.getString(action_name));
                action.setState(rs.getString(state));
                action.setContext(serializer.deserialize(rs.getBytes(context)));
                Timestamp createTime = rs.getTimestamp(gmt_create);
                if (createTime != null) {
                    action.setGmtCreate(new Date(createTime.getTime()));
                }
                Timestamp modifyTime = rs.getTimestamp(gmt_modify);
                if (modifyTime != null) {
                    action.setGmtModify(new Date(modifyTime.getTime()));
                }
                return action;
            }
        });
    }
}
