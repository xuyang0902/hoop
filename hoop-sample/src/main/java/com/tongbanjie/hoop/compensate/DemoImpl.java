package com.tongbanjie.hoop.compensate;

import com.tongbanjie.hoop.api.annotation.GlobalT;
import com.tongbanjie.hoop.api.annotation.TsId;
import com.tongbanjie.hoop.api.enums.HoopType;
import com.tongbanjie.hoop.tcc.confirm.BuyTicketsHook;
import org.springframework.stereotype.Service;

/**
 * @author xu.qiang
 * @date 19/3/26
 */
@Service
public class DemoImpl implements Demo {

    @Override
    @GlobalT(hoopType = HoopType.COMPENSATE,transactionHook = BuyTicketsHook.class)
    public boolean sayHello(@TsId String tsId, String haha) {

        System.out.println(tsId);
        System.out.println(haha);

        //就是扔一个异常出去
//        int index = 10/0;

        return false;
    }
}
