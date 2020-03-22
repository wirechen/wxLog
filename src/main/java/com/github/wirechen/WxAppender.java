package com.github.wirechen;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Author: WireChen
 * @Date: Created in 下午10:44 2019/2/1
 * @Description: wxLog-Appender日志追加器
 */
public class WxAppender extends AppenderSkeleton {

    /**
     * 推送微信模板消息后台接口地址（可以直接改为自己的接口地址，不限于微信模板消息）
     */
    private static final String WX_WARN_URL = "http://wirechen.top/wx/warn/templateMsg";
    /**
     * 个人的openId，多个用英文逗号隔开，在appender的参数中添加
     */
    private String openIds;
    /**
     * 维护一个调用后台接口的线程池，异步请求接口不要影响打日志的正常流程
     */
    private static ThreadPoolExecutor threadPool =
            new ThreadPoolExecutor(0, 10, 10L, TimeUnit.SECONDS, new LinkedBlockingQueue<>());

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
            doPostJsonSync(WX_WARN_URL, json);
        }
    }

    public void close() {
    }

    public boolean requiresLayout() {
        return false;
    }

    /**
     * 发起http请求
     * @param url
     * @param json
     * @return
     */
    public static String doPostJson(String url, String json) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String resultString = "";
        try {
            HttpPost httpPost = new HttpPost(url);
            StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
            httpPost.setEntity(entity);
            response = httpClient.execute(httpPost);
            resultString = EntityUtils.toString(response.getEntity(), "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return resultString;
    }

    public static void doPostJsonSync(String url, String json) {
        threadPool.execute(() -> doPostJson(url, json));
    }
}