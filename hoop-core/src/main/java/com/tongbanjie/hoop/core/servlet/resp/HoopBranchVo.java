package com.tongbanjie.hoop.core.servlet.resp;

import com.alibaba.fastjson.JSON;
import com.tongbanjie.hoop.api.enums.BranchState;
import com.tongbanjie.hoop.api.model.HoopBranch;
import com.tongbanjie.hoop.api.model.HoopContext;
import com.tongbanjie.hoop.core.utils.JavaBaseTypeUtil;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 分布式分支事务model
 *
 * @author xu.qiang
 * @date 18/8/7
 */
public class HoopBranchVo implements Serializable {

    private static final long serialVersionUID = -6895153779632706052L;

    /**
     * 分支事务ID 用UUID
     */
    private String branchId;

    /**
     * 事务ID
     */
    private String tsId;

    /**
     * 应用:beanId:try方法:commit方法:rollBack方法:二阶段顺序:版本号
     * <p>
     * 举例：
     * trade:combineBuyService:buy:commitBuy:rollBackBuy:-1:1.0
     */
    private String actionName;

    /**
     * I:初始，F:完成
     *
     * @see BranchState
     */
    private String state;

    /**
     * 一阶段方法参数json序列化
     */
    private String context;

    /**
     * 创建时间
     */
    private Date gmtCreate;

    /**
     * 修改时间
     */
    private Date gmtModify;


    public String getBranchId() {
        return branchId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }

    public String getTsId() {
        return tsId;
    }

    public void setTsId(String tsId) {
        this.tsId = tsId;
    }

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Date getGmtModify() {
        return gmtModify;
    }

    public void setGmtModify(Date gmtModify) {
        this.gmtModify = gmtModify;
    }

    public static List<HoopBranchVo> getList(List<HoopBranch> list) {

        List<HoopBranchVo> res = new ArrayList<HoopBranchVo>(list.size());
        for (HoopBranch hoopBranch : list) {
            HoopBranchVo vo = new HoopBranchVo();
            BeanUtils.copyProperties(hoopBranch,vo,"context");

            StringBuilder text = new StringBuilder();
            HoopContext context = hoopBranch.getContext();

            Class[] argsTypes = context.getArgsTypes();
            Object[] args = context.getArgs();
            int length = argsTypes.length;
            for(int index = 0 ;index < length; index++ ){

                Class clazz = argsTypes[index];
                Object arg = args[index];
                if(JavaBaseTypeUtil.isClazzBaseType(clazz)){
                    text.append(clazz.getName()).append(":(").append(arg).append(") ");
                }else {
                    text.append(clazz.getName()).append(":(").append(JSON.toJSONString(arg)).append(") ");
                }
            }

            vo.setContext(text.toString());
            res.add(vo);
        }

        return res;
    }
}
