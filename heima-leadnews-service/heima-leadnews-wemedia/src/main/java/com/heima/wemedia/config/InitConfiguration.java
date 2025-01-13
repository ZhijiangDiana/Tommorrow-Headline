package com.heima.wemedia.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @Description
 * @Author 嘉然今天吃向晚
 * @Date 2024/12/29-19:31:13
 */
@Configuration
@ComponentScan("com.heima.apis.article.fallback")
@ComponentScan("com.heima.apis.schedule.fallback")
@ComponentScan("com.heima.apis.admin.fallback")
public class InitConfiguration {
}
