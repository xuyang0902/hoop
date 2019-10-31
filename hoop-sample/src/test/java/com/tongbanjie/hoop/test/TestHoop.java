package com.tongbanjie.hoop.test;

import com.tongbanjie.hoop.api.storage.plugins.repository.TransactionRepositry;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author xu.qiang
 * @date 18/9/18
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/spring/test.xml"})
public class TestHoop {

    @Autowired
    TransactionRepositry transactionRepositry;

    @Test
    public void testDbTransaction(){

        transactionRepositry.removeTransaction("CombineTrade_1809180918596040001");
    }
}
