package com.tongbanjie.hoop.tcc.annotation;

import com.tongbanjie.hoop.api.exception.RollbackException;
import com.tongbanjie.hoop.api.exception.SuspendException;
import com.tongbanjie.hoop.api.utils.TsIdBuilder;
import com.tongbanjie.hoop.tcc.annotation.global.IBuyTicketsGlobalService;
import com.tongbanjie.hoop.tcc.request.BuyRequest;
import com.tongbanjie.hoop.tcc.response.BuyResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * @author xu.qiang
 * @date 18/10/29
 */
public class TccMain {

    private static final Logger logger = LoggerFactory.getLogger(TccMain.class);

    public static void main(String[] args) throws InterruptedException {

        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:/spring/test.xml");

        final IBuyTicketsGlobalService buyTicketsGlobalService = applicationContext.getBean("buyTicketsGlobalService", IBuyTicketsGlobalService.class);


        String orderNo = "123";
        //String tsId = TsIdBuilder.build("TEST", orderNo);


        Map<Integer, BuyRequest> map = new HashMap<Integer, BuyRequest>();
        BuyRequest one = new BuyRequest();
        one.setFromCity("杭州");
        one.setToCity("上海");
        one.setOrderNo(orderNo);
        one.setTbjUserId("yuren");
        one.setTotalAmount(new BigDecimal("100"));
        map.put(1, one);

        BuyRequest two = new BuyRequest();
        two.setFromCity("上海");
        two.setToCity("洛杉矶");
        two.setOrderNo(orderNo);
        two.setTbjUserId("yuren");
        two.setTotalAmount(new BigDecimal("700"));
        map.put(2, two);

        try {
            BuyResponse buy = buyTicketsGlobalService.buy( map,TsIdBuilder.build("E",orderNo));
            System.out.println(">>>购买结果 " + buy);
        } catch (RollbackException e) {
            logger.warn(">>>> 购票失败 e:", e);
        } catch (SuspendException e) {
            logger.warn(">>>> 出票处理中 e:", e);
        } catch (Exception e) {
            logger.warn(">>>> 系统处理中 e", e);
        }


        while (true) {
            Thread.sleep(1000);
        }
    }

}
