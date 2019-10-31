package com.tongbanjie.hoop.core.transaction;

import com.esotericsoftware.reflectasm.MethodAccess;
import com.tongbanjie.hoop.api.model.HoopBranchInvocation;
import com.tongbanjie.hoop.api.transaction.HoopInvoker;
import com.tongbanjie.hoop.core.utils.HoopLogger;
import com.tongbanjie.hoop.core.utils.SpringContextHolder;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 执行 try | commit |rollback方法
 *
 * @author xu.qiang
 * @date 18/8/16
 */
public class HoopCoreInvoker implements HoopInvoker {


    private static final ConcurrentHashMap<String, MethodAccess> invokerMap = new ConcurrentHashMap<String, MethodAccess>(128);

    @Override

    public Object invokeTry(HoopBranchInvocation invocation) throws Exception {

        return hanleMethod(invocation, invocation.getTryMethodName());
    }

    @Override
    public Object invokeCommit(HoopBranchInvocation invocation) throws Exception {
        return hanleMethod(invocation, invocation.getCommitMethodName());
    }

    @Override
    public Object invokeRollback(HoopBranchInvocation invocation) throws Exception {
        return hanleMethod(invocation, invocation.getRollbackMethoName());
    }


    private Object hanleMethod(HoopBranchInvocation invocation, String methodName) throws Exception {
        try {

            String beanId = invocation.getBeanId();
            Object serviceBean = SpringContextHolder.getBean(beanId);
            if (serviceBean == null) {
                throw new RuntimeException("未找到tcc服务service，beanId:" + invocation.getBeanId());
            }

            Class<?> serviceClass = serviceBean.getClass();

            // JDK reflect
            Method method = serviceClass.getMethod(methodName, invocation.getParameterTypes());
            method.setAccessible(true);
            return method.invoke(serviceBean, invocation.getParam());
        } catch (Exception ex) {
            HoopLogger.error("tccInvoker invoker error when execute try|doConfirm|doCancel info:{}", invocation);
            throw ex;
        }
    }

}
