package com.tongbanjie.hoop.tcc.confirm;

import com.alibaba.fastjson.JSON;
import com.tongbanjie.hoop.api.enums.GlobalState;
import com.tongbanjie.hoop.api.model.HoopGlobal;
import com.tongbanjie.hoop.api.recovery.TransactionHook;
import org.springframework.stereotype.Component;

/**
 * @author xu.qiang
 * @date 18/10/29
 */
@Component
public class BuyTicketsHook implements TransactionHook {

    @Override
    public GlobalState hook(HoopGlobal hoopGlobal) {

        /*
         * 检查 航空公司A票是否预定成功
         */

        /*
         * 检查 航空公司B票是否预定成功
         */
        System.out.println("callback:" + JSON.toJSONString(hoopGlobal));

        //都成功
        return GlobalState.ROLLBACK;
    }
}
