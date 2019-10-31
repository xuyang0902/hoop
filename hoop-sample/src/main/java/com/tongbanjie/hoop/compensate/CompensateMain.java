package com.tongbanjie.hoop.compensate;

import com.tongbanjie.hoop.api.utils.TsIdBuilder;
import com.tongbanjie.hoop.tcc.annotation.TccMain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author xu.qiang
 * @date 19/3/26
 */
public class CompensateMain {


    private static final Logger logger = LoggerFactory.getLogger(TccMain.class);

    public static void main(String[] args) throws InterruptedException {

        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:/spring/test.xml");

        final Demo demo = applicationContext.getBean("demoImpl", Demo.class);


        try{

            demo.sayHello(TsIdBuilder.build("HH","8888"),"halla");
        }catch (Exception e){
            logger.error(">>>>> 异常了",e);
        }


        while (true) {
            Thread.sleep(1000);
        }
    }


}
