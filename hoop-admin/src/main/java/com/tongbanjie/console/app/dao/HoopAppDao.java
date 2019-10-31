package com.tongbanjie.console.app.dao;

import com.tongbanjie.console.app.model.HoopApp;
import com.tongbanjie.console.app.model.HoopAppQuery;

import java.util.List;

/**
 * @author xu.qiang
 * @date 18/9/4
 */
public interface HoopAppDao {


    /**
     * 根据查询参数查询数据
     *
     * @param query 查询参数
     */
    List<HoopApp> query(HoopAppQuery query);

    /**
     * 根据ID查询
     *
     * @param id 数据库ID
     */
    HoopApp findById(Long id);

    /**
     * 根据id更新一调数据
     *
     * @param updateParam 更新参数
     */
    int updateByName(HoopApp updateParam);

    /**
     * 新增一条记录
     */
    int insert(HoopApp hoopAppDO);

}
