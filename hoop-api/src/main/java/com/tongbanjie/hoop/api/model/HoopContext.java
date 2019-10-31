package com.tongbanjie.hoop.api.model;

import java.io.Serializable;

/**
 * hoop上下文内容【全局事务和分支事务都有】
 *
 * db序列化为 全局事务方法入参 | 分支事务一阶段入参
 *
 *
 * @author xu.qiang
 * @date 18/9/8
 */
public class HoopContext implements Serializable {


    private static final long serialVersionUID = -7072215403612013243L;

    /**
     * 参数值
     */
    private Object[] args;

    /**
     * 参数类型
     */
    private Class[] argsTypes;


    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    public Class[] getArgsTypes() {
        return argsTypes;
    }

    public void setArgsTypes(Class[] argsTypes) {
        this.argsTypes = argsTypes;
    }
}
