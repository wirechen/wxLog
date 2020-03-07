package com.github.wirechen;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

/**
 * @Author: WireChen
 * @Date: Created in 下午10:44 2019/2/1
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

}