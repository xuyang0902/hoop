package com.tongbanjie.hoop.tcc.annotation.tcc.action;

import com.tongbanjie.hoop.api.annotation.BranchT;
import com.tongbanjie.hoop.api.exception.RollbackException;
import com.tongbanjie.hoop.api.exception.SuspendException;
import com.tongbanjie.hoop.core.utils.HoopLogger;
import com.tongbanjie.hoop.tcc.request.BuyRequest;
import com.tongbanjie.hoop.tcc.response.BuyResponse;
import org.springframework.stereotype.Component;

/**
 * @author xu.qiang
 * @date 18/10/29
 */
@Component
public class BuyAirCompanyA {

    @BranchT(confirm = "doConfirm", cancel = "doCancel", order = Integer.MIN_VALUE)
    public BuyResponse doTry(BuyRequest buyRequest) {

        HoopLogger.info(">>Air A Company 开始预定机票,param:{}", buyRequest);

        BuyResponse buyResponse = new BuyResponse();
        buyResponse.setStatus(BuyResponse.SUCC);
        HoopLogger.info(">>Air A Company 执行 rpc调用 param:{} rpc返回结果:res:{}", buyRequest, buyResponse);

        if (buyResponse.getStatus().equals(BuyResponse.Failed)) {
            throw new RollbackException("航空公司A出票失败-->份额不够了");
        } else if (buyResponse.getStatus().equals(BuyResponse.Proccess) || buyResponse.getStatus().equals(BuyResponse.UNKNOW)) {
            throw new SuspendException("航空公司A出票处理中 挂起");
        }

        return buyResponse;
    }

    public void doConfirm(BuyRequest buyRequest) {
        HoopLogger.info(">>Air A Company 开始执行commit操作,param:{}", buyRequest);
    }

    public void doCancel(BuyRequest buyRequest) {
        HoopLogger.info(">>Air A Company 开始执行cancel操作,param:{}", buyRequest);
    }


}
