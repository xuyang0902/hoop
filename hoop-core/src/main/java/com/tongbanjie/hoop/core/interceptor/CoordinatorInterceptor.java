package com.tongbanjie.hoop.core.interceptor;

import com.tongbanjie.hoop.api.annotation.BranchT;
import com.tongbanjie.hoop.api.enums.BranchState;
import com.tongbanjie.hoop.api.enums.GlobalState;
import com.tongbanjie.hoop.api.model.HoopBranch;
import com.tongbanjie.hoop.api.model.HoopContext;
import com.tongbanjie.hoop.api.transaction.HoopTransaction;
import com.tongbanjie.hoop.api.transaction.HoopTransactionManager;
import com.tongbanjie.hoop.api.utils.ActionNameUtil;
import com.tongbanjie.hoop.api.utils.UUIDUtil;
import com.tongbanjie.hoop.core.utils.HoopMethodUtil;
import com.tongbanjie.hoop.core.utils.ReflectionUtil;
import com.tongbanjie.hoop.core.utils.SpringContextHolder;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.core.Ordered;
import org.springframework.util.Assert;

import java.lang.reflect.Method;

/**
 * 协调管理器
 *
 * @author xu.qiang
 * @date 18/4/12
 */
public class CoordinatorInterceptor implements Ordered {

    private HoopTransactionManager transactionManager;

    public CoordinatorInterceptor(HoopTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public Object interceptTryMethod(ProceedingJoinPoint pjp) throws Throwable {

        HoopTransaction transaction = transactionManager.getCurrentTransaction();

        //存在全局事务且事务状态为初始化 加入分支事务
        if (transaction != null && transaction.getHoopGlobal().getState().equals(GlobalState.INIT.getCode())) {
            enlistParticipant(pjp, transaction);
        }

        return pjp.proceed(pjp.getArgs());
    }

    /**
     * 加入事务参与者
     *
     * @param pjp
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    private void enlistParticipant(ProceedingJoinPoint pjp, HoopTransaction transaction) throws Exception {

        Method method = HoopMethodUtil.getMethodByAnnotation(pjp, BranchT.class);
        BranchT branchT = method.getAnnotation(BranchT.class);

        Class<?> targetClass = ReflectionUtil.getTargetClass(pjp.getTarget());
        Object bean = SpringContextHolder.getBean(targetClass);
        Assert.notNull(bean, "branch未设置二阶段的beanId，actionClass：" + targetClass);

        Object[] args = pjp.getArgs();

        //把分支事务开启的入参 序列化到db
        HoopContext hoopContext = new HoopContext();
        hoopContext.setArgs(args);
        hoopContext.setArgsTypes(method.getParameterTypes());

        HoopBranch hoopBranch = new HoopBranch();
        hoopBranch.setContext(hoopContext);
        hoopBranch.setActionName(ActionNameUtil.getActionName(branchT, targetClass, method.getName()));
        hoopBranch.setBranchId(UUIDUtil.getUUId());
        hoopBranch.setState(BranchState.INIT.getCode());

        transactionManager.enlistParticipant(hoopBranch);

    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
