package com.heima.article.config;

import com.heima.article.interceptor.RequestTimeInterceptor;
import com.heima.article.interceptor.TokenInterceptor;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@ComponentScan("com.heima.apis")
public class WebMvcConfiguration implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new TokenInterceptor())
                .addPathPatterns("/**");
        registry.addInterceptor(new RequestTimeInterceptor())
                .addPathPatterns("/**");
    }
}
