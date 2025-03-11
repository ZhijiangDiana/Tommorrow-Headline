package com.heima.comment.interceptor;

import com.heima.model.user.pojos.ApUser;
import com.heima.utils.thread.ThreadLocalUtil;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AppTokenInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Integer userId = Integer.valueOf(request.getHeader("userId"));
        if(userId != null){
            //存入到当前线程中
            ThreadLocalUtil.setUserId(userId);
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        ThreadLocalUtil.rmUserId();
    }
}
