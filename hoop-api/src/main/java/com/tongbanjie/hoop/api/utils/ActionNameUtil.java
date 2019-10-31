package com.tongbanjie.hoop.api.utils;

import com.tongbanjie.hoop.api.annotation.BranchT;
import com.tongbanjie.hoop.api.model.HoopBranch;
import com.tongbanjie.hoop.api.model.HoopBranchInvocation;
import com.tongbanjie.hoop.api.model.HoopContext;
import org.apache.commons.lang.StringUtils;

import java.util.*;

/**
 * actionName工具类
 *
 * @author xu.qiang
 * @date 18/8/16
 */
public class ActionNameUtil {

    /**
     * 获取二阶段调用list
     *
     * @param actions
     * @return
     * @throws ClassNotFoundException
     */
    public static List<HoopBranchInvocation> getTwoPhaseInvocation(List<HoopBranch> actions) throws ClassNotFoundException {

        List<HoopBranchInvocation> actionInvocations = new ArrayList<HoopBranchInvocation>();

        for (HoopBranch action : actions) {
            actionInvocations.add(get(action));
        }

        /*
         * 二阶段提交顺序 根据order从小到大执行
         */
        Collections.sort(actionInvocations, new Comparator<HoopBranchInvocation>() {
            @Override
            public int compare(HoopBranchInvocation o1, HoopBranchInvocation o2) {
                return o1.getOrder().compareTo(o2.getOrder());
            }
        });

        return actionInvocations;

    }


    /**
     * 应用:beanId:try方法:commit方法:rollBack方法:二阶段的入参class:二阶段顺序:版本号
     * <p>
     * 举例：
     * trade:combineBuyService:buy:commitBuy:rollBackBuy:-1:1.0
     */
    private static HoopBranchInvocation get(HoopBranch action) throws ClassNotFoundException {

        /*
         * 解析actionName
         */
        String[] split = action.getActionName().split(":");
        HoopBranchInvocation actionInvocation = new HoopBranchInvocation();
        actionInvocation.setAppName(split[0]);
        actionInvocation.setBeanId(split[1]);
        actionInvocation.setTryMethodName(split[2]);
        actionInvocation.setCommitMethodName(split[3]);
        actionInvocation.setRollbackMethoName(split[4]);
        actionInvocation.setOrder(Long.valueOf(split[5]));
        actionInvocation.setVersion(String.valueOf(split[6]));

        /*
         * 二阶段的调用参数
         */
        HoopContext context = action.getContext();
        if (context != null) {
            actionInvocation.setParam(context.getArgs());
            actionInvocation.setParameterTypes(context.getArgsTypes());
        }

        actionInvocation.setHoopBranch(action);
        return actionInvocation;
    }


    /**
     * 获取actionName
     *
     * @param branchT
     * @param targetClass
     * @param tryMethodName
     * @return
     */
    public static String getActionName(BranchT branchT, Class<?> targetClass, String tryMethodName) {

        String beanId = branchT.beanId();
        if (beanId == null || "".equals(beanId)) {
            beanId = StringUtils.uncapitalize(targetClass.getSimpleName());
        }

        /*
         * 应用服务名称:beanId:try方法:commit方法:rollBack方法:二阶段顺序:版本号
         */
        StringBuilder sb = new StringBuilder();
        sb.append(branchT.app()).append(":");
        sb.append(beanId).append(":");
        sb.append(tryMethodName).append(":");
        sb.append(branchT.confirm()).append(":");
        sb.append(branchT.cancel()).append(":");
        sb.append(branchT.order()).append(":");
        sb.append(branchT.version()).append(":");

        return sb.toString();
    }

}
