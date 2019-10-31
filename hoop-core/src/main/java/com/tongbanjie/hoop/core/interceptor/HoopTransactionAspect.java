package com.tongbanjie.hoop.core.interceptor;

import com.tongbanjie.hoop.api.annotation.GlobalT;
import com.tongbanjie.hoop.api.config.HoopClientConfig;
import com.tongbanjie.hoop.api.enums.HoopType;
import com.tongbanjie.hoop.api.exception.HoopException;
import com.tongbanjie.hoop.api.recovery.TransactionHook;
import com.tongbanjie.hoop.api.transaction.HoopTransactionManager;
import com.tongbanjie.hoop.core.utils.HoopMethodUtil;
import com.tongbanjie.hoop.core.utils.SpringContextHolder;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;

import java.lang.reflect.Method;

/**
 * hoop事务切面
 *
 * @author xu.qiang
 * @date 18/4/12
 */
@Aspect
public class HoopTransactionAspect implements PriorityOrdered {

    private HoopTransactionInterceptor hoopTransactionInterceptor;
    private HoopTransactionManager transactionManager;

    @Pointcut("@annotation(com.tongbanjie.hoop.api.annotation.GlobalT)")
    public void beginActivity() {
    }

    @Around("beginActivity()")
    public Object interceptTryMethod(ProceedingJoinPoint pjp) throws Throwable {

        Method method = HoopMethodUtil.getMethodByAnnotation(pjp, GlobalT.class);
        GlobalT globalT = method.getAnnotation(GlobalT.class);

        Object recoveryBean = SpringContextHolder.getBean(globalT.transactionHook());
        if (recoveryBean != null && !(recoveryBean instanceof TransactionHook)) {
            throw new RuntimeException("transactionHook not implements TransactionHook，confirmBean:" + recoveryBean.getClass().getName());
        }

        if (transactionManager.isActive()) {
            throw new HoopException("hoop not support multiple global transaction ");
        }

        String code = globalT.hoopType().getCode();
        if (HoopType.TCC.getCode().equals(code) || HoopType.COMPENSATE.getCode().equals(code)) {

            if (hoopTransactionInterceptor == null) {
                synchronized (this){
                    hoopTransactionInterceptor = new HoopTransactionInterceptor(transactionManager);
                }
            }

            return hoopTransactionInterceptor.handleTransaction(pjp, method, globalT);

        } else {
            throw new HoopException("not support hooptype code: " + code);
        }
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
