package com.tongbanjie.hoop.core.servlet;

import com.alibaba.fastjson.JSONObject;
import com.tongbanjie.hoop.api.model.HoopBranch;
import com.tongbanjie.hoop.api.model.HoopGlobal;
import com.tongbanjie.hoop.api.model.query.GlobalTransactionQuery;
import com.tongbanjie.hoop.api.storage.plugins.repository.BranchTransactionDao;
import com.tongbanjie.hoop.api.storage.plugins.repository.GlobalTransactionDao;
import com.tongbanjie.hoop.core.servlet.constant.ReqType;
import com.tongbanjie.hoop.core.servlet.resp.HoopBranchVo;
import com.tongbanjie.hoop.core.servlet.resp.HoopGlobalInfo;
import com.tongbanjie.hoop.core.servlet.resp.HoopGlobalVo;
import com.tongbanjie.hoop.core.utils.HttpRemotingUtils;
import com.tongbanjie.hoop.core.utils.SpringContextHolder;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 主要是用于查询某些activity数据 action数据
 *
 * @author xu.qiang
 * @date 18/8/31
 */
public class HoopServlet extends HttpServlet {

    private static final long serialVersionUID = 9048286703585606353L;

    /**
     * spring 上下文信息
     */
    private ApplicationContext context;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        JSONObject requestJson = HttpRemotingUtils.getRequestFromHttpRequest(req);
        String requestType = requestJson.getString("reqType");


        if (ReqType.HEALTHY_CHECK.equals(requestType)) {

            HttpRemotingUtils.writeResponse(resp, "OK");

        } else if (ReqType.ACTIVITY_LOG.equals(requestType)) {

            GlobalTransactionQuery query = new GlobalTransactionQuery();

            query.setStartTsId(requestJson.getString("startTsId"));
            query.setEltModifyTime(requestJson.getString("eltModifyTime"));
            query.setTsId(requestJson.getString("tsId"));
            query.setTsType(requestJson.getString("tsType"));
            String stateList = requestJson.getString("stateList");
            if (!StringUtils.isEmpty(stateList)) {
                String[] split = stateList.split(",");
                List<String> states = new ArrayList<String>();
                for (String s : split) {
                    states.add(s);
                }
                query.setStateList(states);
            }
            Integer eltRecoverCount = requestJson.getInteger("eltRecoverCount");
            if (eltRecoverCount != null) {
                query.setEltRecoverCount(eltRecoverCount);
            }

            Integer offset = requestJson.getInteger("offset");
            if (offset != null) {
                query.setOffset(offset);
            }
            Integer limit = requestJson.getInteger("limit");
            if (limit != null) {
                query.setLimit(limit);
            }

            HoopGlobalInfo info = new HoopGlobalInfo();
            List<HoopGlobal> list = SpringContextHolder.getBean(GlobalTransactionDao.class).query(query);
            info.setList(HoopGlobalVo.getList(list));
            info.setCount(SpringContextHolder.getBean(GlobalTransactionDao.class).count(query));

            HttpRemotingUtils.writeResponse(resp, info);

        } else if (ReqType.ACTION_LOG.equals(requestType)) {

            String tsId = requestJson.getString("tsId");

            HoopGlobal activityInfo = context.getBean(GlobalTransactionDao.class).selectByTsId(tsId);
            List<HoopBranch> actions = context.getBean(BranchTransactionDao.class).select(tsId);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("activityInfo", HoopGlobalVo.getVO(activityInfo));
            jsonObject.put("actions", HoopBranchVo.getList(actions));

            HttpRemotingUtils.writeResponse(resp, jsonObject);

        } else if (ReqType.STOP_RECOVER.equals(requestType)) {

            // TODO: 18/10/10  
            HttpRemotingUtils.writeResponse(resp, "OK");

        } else if (ReqType.RUN_RECOVER.equals(requestType)) {

            // TODO: 18/10/10
            HttpRemotingUtils.writeResponse(resp, "OK");

        }

    }

    @Override
    public void init() throws ServletException {
        super.init();

        context = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
        if (context == null) {
            throw new RuntimeException("error: 接入hoop-console ,web容器上下文未初始化");
        }
    }

}
