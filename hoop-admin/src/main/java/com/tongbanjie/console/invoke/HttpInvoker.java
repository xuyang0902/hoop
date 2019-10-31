package com.tongbanjie.console.invoke;

import com.tongbanjie.console.app.dao.HoopAppDao;
import com.tongbanjie.console.app.model.HoopApp;
import com.tongbanjie.console.util.HttpClientUtils;
import com.tongbanjie.console.util.RandomIndexUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author xu.qiang
 * @date 18/10/10
 */
@Component
public class HttpInvoker {

    private static final Logger logger = LoggerFactory.getLogger(HttpInvoker.class);

    @Autowired
    private HoopAppDao hoopAppDao;

    public String invoke(Long appId, String body) throws Exception {

        HoopApp app = hoopAppDao.findById(appId);
        String randomUrl = RandomIndexUtils.getRandomUrl(app.getUrls());

        String resJson = null;
        try {

            resJson = HttpClientUtils.post(randomUrl, body, "application/json", "utf-8",
                    5000, 5000);

            return resJson;
        } catch (Exception e) {
            logger.error(">>> 远程调用http error,url{},body:{},e:", randomUrl, body, e);
            throw e;
        }

    }

}
