package com.heima.search.filter;

import com.heima.model.user.pojos.ApUser;
import com.heima.utils.thread.ThreadLocalUtil;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ApTokenInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String userId = request.getHeader("userId");
        if (userId != null) {
            ApUser apUser = new ApUser();
            apUser.setId(Integer.parseInt(userId));
            ThreadLocalUtil.setObject(apUser);
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        ThreadLocalUtil.rmObject();
    }
}