package com.tongbanjie.hoop.core.servlet.constant;

/**
 * 请求类型
 * @author xu.qiang
 * @date 18/8/31
 */
public interface ReqType {

    /**
     * 健康检查  查看目标系统是否存活
     */
    String HEALTHY_CHECK = "HEALTHY_CHECK";

    /**
     * 查询activity日志
     */
    String ACTIVITY_LOG = "ACTIVITY_LOG";

    /**
     * 查询action日志
     */
    String ACTION_LOG = "ACTION_LOG";


    /**
     * 停止恢复线程工作
     */
    String STOP_RECOVER = "STOP_RECOVER";

    /**
     * 开始恢复线程
     */
    String RUN_RECOVER = "RUN_RECOVER";

}
