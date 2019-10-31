package com.tongbanjie.console.app.dao.impl;

import com.tongbanjie.console.app.dao.HoopAppDao;
import com.tongbanjie.console.app.model.HoopApp;
import com.tongbanjie.console.app.model.HoopAppQuery;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author xu.qiang
 * @date 18/9/4
 */
@Repository
public class HoopAppDaoImpl extends SqlSessionDaoSupport implements HoopAppDao {

    @Override
    public List<HoopApp> query(HoopAppQuery query) {

        Map<String, Object> queryMap = new HashMap<String, Object>();
        queryMap.put("id", query.getId());
        queryMap.put("appName", query.getAppName());
        queryMap.put("offset", query.getOffset());
        queryMap.put("limit", query.getLimit());

        return getSqlSession().selectList("model.HoopApp.QUERY", query);
    }

    @Override
    public HoopApp findById(Long id) {
        return (HoopApp) getSqlSession().selectOne("model.HoopApp.FIND_BY_ID", id);
    }

    @Override
    public int updateByName(HoopApp updateParam) {
        Assert.notNull(updateParam.getAppName(),"更新appinfo id不能为空");
        updateParam.setModifyTime(new Date());
        return getSqlSession().update("model.HoopApp.UPDATE_BY_NAME", updateParam);
    }

    @Override
    public int insert(HoopApp hoopAppDO) {
        Date now = new Date();
        hoopAppDO.setCreateTime(now);
        hoopAppDO.setModifyTime(now);
        return getSqlSession().insert("model.HoopApp.INSERT",hoopAppDO);
    }
}
