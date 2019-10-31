package com.tongbanjie.hoop.tcc.annotation.global;

import com.tongbanjie.hoop.tcc.request.BuyRequest;
import com.tongbanjie.hoop.tcc.response.BuyResponse;

import java.util.Map;

/**
 * @author xu.qiang
 * @date 18/10/30
 */
public interface IBuyTicketsGlobalService {


    BuyResponse buy( Map<Integer, BuyRequest> integerBuyRequestMap,String orderNo);

}
