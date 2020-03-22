package com.github.wirechen;

import org.apache.log4j.Logger;

/**
 * @Author: WireChen
 * @Date: Created in 下午10:45 2019/2/1
 * @Description: 测试
 */
public class Test {

    private static Logger log = Logger.getLogger(Test.class);

    public static void main(String[] args) {
        // 不是指定日志级别-不报警
        log.info("Here is a info log");
        // 指定级别、指定开头内容-触发报警
        log.warn("【微信报警测试】Welcome to use wxLog!");
        // 不是指定日志开头-不报警
        log.warn("Here is a warn log");
        // 不是指定日志级别-不报警
        log.error("Here is a error log");
    }
}
