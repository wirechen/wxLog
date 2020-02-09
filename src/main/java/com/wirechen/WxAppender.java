package com.wirechen;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;

/**
 * @Author: WireChen
 * @Date: Created in 下午10:44 2019/2/1
 * @Description: 微信log4j appender
 */
public class WxAppender extends AppenderSkeleton {

    private static Logger log = Logger.getLogger(WxAppender.class);

    private static final String WX_WARN_URL = "http://wirechen.top/wx/warn/templateMsg";

    private String openIds; // 逗号分割的OPEN_ID

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
            // 异步推送，不能影响打日志
            HttpClientUtil.doPostJsonSync(WX_WARN_URL, json);
        }
    }

    public void close() {

    }

    public boolean requiresLayout() {
        return false;
    }

    public static void main(String[] args) {
        log.info("11111");
        // 推送微信报警
        log.warn("bbbbbbbbbbbbb");
        log.error("22222");
    }

}