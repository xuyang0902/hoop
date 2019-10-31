package com.tongbanjie.hoop.core.interceptor;

import com.tongbanjie.hoop.api.annotation.GlobalT;
import com.tongbanjie.hoop.api.annotation.TsId;
import com.tongbanjie.hoop.api.config.HoopClientConfig;
import com.tongbanjie.hoop.api.exception.HoopTimeOutException;
import com.tongbanjie.hoop.api.exception.RollbackException;
import com.tongbanjie.hoop.api.model.HoopContext;
import com.tongbanjie.hoop.api.model.HoopGlobal;
import com.tongbanjie.hoop.api.transaction.HoopTransactionManager;
import com.tongbanjie.hoop.api.utils.UUIDUtil;
import com.tongbanjie.hoop.core.transaction.HoopSession;
import com.tongbanjie.hoop.core.utils.HoopExceptionUtil;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.aspectj.lang.ProceedingJoinPoint;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * hoop事务拦截器
 *
 * @author xu.qiang
 * @date 18/4/12
 */
public class HoopTransactionInterceptor {

    private HoopTransactionManager transactionManager;

    public HoopTransactionInterceptor(HoopTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public Object handleTransaction(ProceedingJoinPoint pjp, Method method, GlobalT globalT) throws Throwable {

        Object returnValue = null;
        boolean asyn = globalT.asynC();

        try {

            //01.开启全局事务
            HoopGlobal hoopGlobal = this.begin(pjp, method, globalT);

            try {

                //02.业务逻辑处理  编排 分支的try动作
                returnValue = pjp.proceed();

            } catch (Exception tryException) {

                //异常处理 回滚 | 挂起
                return exceptionHandler(globalT, tryException, asyn);
            }

            // 全局事务超时处理
            long tryTime = System.currentTimeMillis() - hoopGlobal.getStartTime().getTime();
            if (tryTime > (hoopGlobal.getTimeout() * 1000L)) {
                throw new HoopTimeOutException(String.format("Hoop tsId:%s 一阶段执行时间:%s ms,事务超时时间：%s秒 ", hoopGlobal.getTsId(), tryTime, hoopGlobal.getTimeout()));
            }

            //03.所有正常操作完毕 提交事务
            transactionManager.commit(asyn);

            return returnValue;

        } finally {

            //当前线程移除transaction
            transactionManager.cleanAfterCompletion(HoopSession.getHoopTransaction());
        }
    }


    /**
     * 开启全局事务
     *
     * @param pjp
     * @param method
     * @param globalT
     * @return
     */
    private HoopGlobal begin(ProceedingJoinPoint pjp, Method method, GlobalT globalT) {

        String tsId = null;
        Object[] args = pjp.getArgs();
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        for (int index = 0; index < parameterAnnotations.length; index++) {
            for (int y = 0; y < parameterAnnotations[index].length; y++) {
                Annotation annotation = parameterAnnotations[index][y];
                //获取tsId
                if (annotation instanceof TsId) {
                    tsId = String.valueOf(args[index]);
                }
            }
        }

        if (tsId == null) {
            tsId = UUIDUtil.getUUId();
        }

        //把全局事务开启的入参 序列化到db
        HoopContext hoopContext = new HoopContext();
        hoopContext.setArgs(args);
        hoopContext.setArgsTypes(method.getParameterTypes());

        HoopGlobal hoopGlobal = HoopGlobal.build(tsId,
                hoopContext,
                globalT.transactionHook(),
                globalT.env(),
                globalT.hoopType().getCode(),
                globalT.timeout());
        return transactionManager.begin(hoopGlobal);//开启事务
    }

    /**
     * 全局事务主流程异常处理
     *
     * @param globalT
     * @param tryException
     * @param asyn
     * @return
     * @throws Exception
     */
    private Object exceptionHandler(GlobalT globalT,
                                    Exception tryException,
                                    boolean asyn)
            throws Exception {

        if (HoopExceptionUtil.mustRollbackFor(tryException) || HoopExceptionUtil.needRollbackFor(tryException, globalT)) {
            transactionManager.rollback(asyn);
        }

        throw tryException;
    }



}
