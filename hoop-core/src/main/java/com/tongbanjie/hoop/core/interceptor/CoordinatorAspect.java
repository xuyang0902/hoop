package com.tongbanjie.hoop.core.interceptor;

import com.tongbanjie.hoop.api.transaction.HoopTransactionManager;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;

/**
 * 协调者切面
 *
 * @author xu.qiang
 * @date 18/4/12
 */
@Aspect
public class CoordinatorAspect implements PriorityOrdered {

    private CoordinatorInterceptor coordinatorInterceptor;

    private HoopTransactionManager transactionManager;

    @Pointcut("@annotation(com.tongbanjie.hoop.api.annotation.BranchT)")
    public void tryMethod() {
    }

    @Around("tryMethod()")
    public Object hanle(ProceedingJoinPoint pjp) throws Throwable {
        if (this.coordinatorInterceptor == null) {
            this.coordinatorInterceptor = new CoordinatorInterceptor(this.transactionManager);
        }
        return this.coordinatorInterceptor.interceptTryMethod(pjp);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 1;
    }

    public HoopTransactionManager getTransactionManager() {
        return transactionManager;
    }

    public void setTransactionManager(HoopTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }
}
