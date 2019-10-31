package com.tongbanjie.console.web.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tongbanjie.console.app.dao.HoopAppDao;
import com.tongbanjie.console.app.model.HoopApp;
import com.tongbanjie.console.app.model.HoopAppQuery;
import com.tongbanjie.console.invoke.HttpInvoker;
import com.tongbanjie.console.web.model.Page;
import com.tongbanjie.hoop.core.servlet.constant.ReqType;
import com.tongbanjie.hoop.core.servlet.resp.HoopGlobalVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 查询事务日志记录
 *
 * @author xu.qiang
 * @date 18/9/4
 */
@Controller
public class GlobalTransactionController {

    private static final Logger logger = LoggerFactory.getLogger(GlobalTransactionController.class);

    @Autowired
    private HoopAppDao hoopAppDao;

    @Autowired
    private HttpInvoker httpInvoker;

    @RequestMapping(value = "/activity")
    public String getActivity(HttpServletRequest request, Model model) throws Exception {

        String appName = request.getParameter("appName");
        String status = request.getParameter("status");
        String pageNo = request.getParameter("pageNo");

        Integer indexPage = 1;//默认为第一页
        if (!StringUtils.isEmpty(pageNo)) {
            indexPage = Integer.valueOf(pageNo);
        }

        if (StringUtils.isEmpty(appName)) {
            appName = null;
        }

        if (StringUtils.isEmpty(status)) {
            status = null;
        }

        Page page = Page.getInstance(indexPage, null, 15);

        /**
         * appName没有传的时候默认取第一条,传的时候就默认只有一条
         */
        HoopAppQuery appQuery = new HoopAppQuery();
        appQuery.setAppName(appName);
        List<HoopApp> list = hoopAppDao.query(appQuery);
        if (!CollectionUtils.isEmpty(list)) {

            HoopApp hoopApp = list.get(0);

            JSONObject json = new JSONObject();
            json.put("reqType", ReqType.ACTIVITY_LOG);
            json.put("stateList", status);
            json.put("offset", page.getOffset());
            json.put("limit", page.getPageSize());
            String resJson = httpInvoker.invoke(hoopApp.getId(), json.toString());

            JSONObject jsonRes = JSONObject.parseObject(resJson);
            Integer count = jsonRes.getInteger("count");
            List<HoopGlobalVo> activityList = JSON.parseArray(jsonRes.getString("list"), HoopGlobalVo.class);

            model.addAttribute("list", activityList);
            model.addAttribute("appId", hoopApp.getId());
            model.addAttribute("appName", appName);
            model.addAttribute("applist", hoopAppDao.query(new HoopAppQuery()));
            model.addAttribute("app", hoopApp);
            model.addAttribute("status", status);
            page.setTotalSize(count);
            model.addAttribute("page", page);
        }

        return "pages/activity";

    }


}
