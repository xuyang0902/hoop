package com.tongbanjie.console.web.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tongbanjie.console.app.dao.HoopAppDao;
import com.tongbanjie.console.app.model.HoopApp;
import com.tongbanjie.console.invoke.HttpInvoker;
import com.tongbanjie.hoop.core.servlet.constant.ReqType;
import com.tongbanjie.hoop.core.servlet.resp.HoopBranchVo;
import com.tongbanjie.hoop.core.servlet.resp.HoopGlobalVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * 查询action数据
 *
 * @author xu.qiang
 * @date 18/9/4
 */
@Controller
public class BranchTransactionController {

    private static final Logger logger = LoggerFactory.getLogger(BranchTransactionController.class);

    @Autowired
    private HoopAppDao hoopAppDao;

    @Autowired
    private HttpInvoker httpInvoker;

    @RequestMapping(value = "/action/{tsId}/{appId}")
    public String getActivity(@PathVariable String tsId, @PathVariable Long appId, Model model) throws Exception {


        HoopApp app = hoopAppDao.findById(appId);

        JSONObject json = new JSONObject();
        json.put("reqType", ReqType.ACTION_LOG);
        json.put("tsId", tsId);

        String resJson = httpInvoker.invoke(app.getId(), json.toString());

        JSONObject jsonRes = JSONObject.parseObject(resJson);
        List<HoopBranchVo> actionList = JSON.parseArray(jsonRes.getString("actions"), HoopBranchVo.class);
        HoopGlobalVo activityInfo = JSON.parseObject(jsonRes.getString("activityInfo"), HoopGlobalVo.class);

        model.addAttribute("actionList", actionList);
        model.addAttribute("activity", activityInfo);
        model.addAttribute("appName", app.getAppName());

        return "pages/action";

    }


}
