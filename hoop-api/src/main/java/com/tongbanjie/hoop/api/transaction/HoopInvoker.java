package com.tongbanjie.hoop.api.transaction;

import com.tongbanjie.hoop.api.model.HoopBranchInvocation;

/**
 * 调用者
 *
 * @author xu.qiang
 * @date 18/4/13
 */
public interface HoopInvoker {

    Object invokeTry(HoopBranchInvocation invocation) throws Exception;

    Object invokeCommit(HoopBranchInvocation invocation) throws Exception;

    Object invokeRollback(HoopBranchInvocation invocation) throws Exception;

}
