package com.tongbanjie.hoop.tcc.annotation.global;

import com.tongbanjie.hoop.api.annotation.GlobalT;
import com.tongbanjie.hoop.api.annotation.TsId;
import com.tongbanjie.hoop.api.exception.RollbackException;
import com.tongbanjie.hoop.tcc.annotation.tcc.action.BuyAirCompanyA;
import com.tongbanjie.hoop.tcc.annotation.tcc.action.BuyAirCompanyB;
import com.tongbanjie.hoop.tcc.confirm.BuyTicketsHook;
import com.tongbanjie.hoop.tcc.request.BuyRequest;
import com.tongbanjie.hoop.tcc.response.BuyResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author xu.qiang
 * @date 18/10/30
 */
@Service
public class BuyTicketsGlobalService implements IBuyTicketsGlobalService {


    @Autowired
    private BuyAirCompanyA buyAirCompanyA;

    @Autowired
    private BuyAirCompanyB buyAirCompanyB;

    @GlobalT(transactionHook = BuyTicketsHook.class, rollbackFor = RollbackException.class)
    @Override
    public BuyResponse buy(Map<Integer, BuyRequest> integerBuyRequestMap, @TsId String orderNo) {

        BuyRequest buyRequestA = integerBuyRequestMap.get(1);
        BuyRequest buyRequestB = integerBuyRequestMap.get(2);

        BuyResponse buyResponseA = buyAirCompanyA.doTry(buyRequestA);
        System.out.println("--->航空公司A预定结果：" + buyResponseA);

        BuyResponse buyReB = buyAirCompanyB.doTry(buyRequestB);
        System.out.println("--->航空公司A预定结果：" + buyReB);

        BuyResponse buyResponse = new BuyResponse();
        buyResponse.setStatus(BuyResponse.SUCC);
        return buyResponse;
    }

}
