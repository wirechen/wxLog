package com.github.wirechen;

import org.apache.log4j.Level;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;

/**
 * @Auther: wirechen
 * @Date: 2019/2/2 14:54
 * @Description: 自定义过滤器
 */
public class WxMatchFilter extends Filter {

    /**
     * 包名过滤 多个用逗号隔开
     */
    String packageToMatch;
    /**
     * 日志内容过滤 多个用逗号隔开
     */
    String stringToMatch;
    /**
     * 最小等级报警，默认WARN
     */
    Level levelMin = Level.WARN;
    /**
     * 最大等级报警，默认ERROR
     */
    Level levelMax = Level.ERROR;

    public String getPackageToMatch() {
        return packageToMatch;
    }

    public void setPackageToMatch(String packageToMatch) {
        this.packageToMatch = packageToMatch;
    }

    public String getStringToMatch() {
        return stringToMatch;
    }

    public void setStringToMatch(String stringToMatch) {
        this.stringToMatch = stringToMatch;
    }

    public Level getLevelMin() {
        return levelMin;
    }

    public void setLevelMin(Level levelMin) {
        this.levelMin = levelMin;
    }

    public Level getLevelMax() {
        return levelMax;
    }

    public void setLevelMax(Level levelMax) {
        this.levelMax = levelMax;
    }

    @Override
    public int decide(LoggingEvent event) {
        if (checkString(event) && checkPackage(event) && checkLevelRange(event)) {
            return 0;
        } else {
            return -1;
        }
    }

    /**
     * 过滤日志内容
     * @param event
     * @return
     */
    private Boolean checkString(LoggingEvent event) {
        if (stringToMatch != null) {
            String message = event.getMessage().toString();
            for (String content : stringToMatch.split(",")) {
                if (message.indexOf(content) != -1) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }

    /**
     * 过滤包名
     * @param event
     * @return
     */
    private Boolean checkPackage(LoggingEvent event) {
        if (packageToMatch != null) {
            String packageName = event.getLocationInformation().getClassName();
            for (String match : packageToMatch.split(",")) {
                if (packageName.startsWith(match)) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }

    /**
     * 过滤日志级别
     * @param event
     * @return
     */
    private Boolean checkLevelRange(LoggingEvent event) {
        Level logLevel = event.getLevel();
        if (logLevel.toInt() >= levelMin.toInt() && logLevel.toInt() <= levelMax.toInt()) {
            return true;
        } else {
            return false;
        }
    }
}
