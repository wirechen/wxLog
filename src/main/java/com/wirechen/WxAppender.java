package com.wirechen;

import org.apache.log4j.AppenderSkeleton;
<<<<<<< HEAD
=======
import org.apache.log4j.Logger;
>>>>>>> 749ff5cd6f9af2da5d6782826ebc1a427ac72dc1
import org.apache.log4j.spi.LoggingEvent;

/**
 * @Author: WireChen
 * @Date: Created in 下午10:44 2019/2/1
<<<<<<< HEAD
 * @Description: 自定义Appender
 */
public class WxAppender extends AppenderSkeleton {

    /**
     * 推送微信模板消息后台接口地址
     */
    private static final String WX_WARN_URL = "http://wirechen.top/wx/warn/templateMsg";
    /**
     * 个人的openId，多个用英文逗号分割
     */
    private String openIds;
=======
 * @Description: 微信log4j appender
 */
public class WxAppender extends AppenderSkeleton {

    private static Logger log = Logger.getLogger(WxAppender.class);

    private static final String WX_WARN_URL = "http://wirechen.top/wx/warn/templateMsg";

    private String openIds; // 逗号分割的OPEN_ID
>>>>>>> 749ff5cd6f9af2da5d6782826ebc1a427ac72dc1

    public String getOpenIds() {
        return openIds;
    }

    public void setOpenIds(String openIds) {
        this.openIds = openIds;
    }

    protected void append(LoggingEvent loggingEvent) {
        String message = loggingEvent.getLocationInformation().fullInfo + "  " + loggingEvent.getMessage().toString();
        String json = "{\"openId\":\"{OPENID}\",\"msg\":\"{MSG}\"}";
        for (String openId : openIds.split(",")) {
            json = json.replace("{OPENID}", openId).replace("{MSG}", message);
<<<<<<< HEAD
            HttpClientUtil.doPostJson(WX_WARN_URL, json);
=======
            // 异步推送，不能影响打日志
            HttpClientUtil.doPostJsonSync(WX_WARN_URL, json);
>>>>>>> 749ff5cd6f9af2da5d6782826ebc1a427ac72dc1
        }
    }

    public void close() {

    }

    public boolean requiresLayout() {
        return false;
    }
<<<<<<< HEAD
=======

    public static void main(String[] args) {
        log.info("11111");
        // 推送微信报警
        log.warn("bbbbbbbbbbbbb");
        log.error("22222");
    }

>>>>>>> 749ff5cd6f9af2da5d6782826ebc1a427ac72dc1
}