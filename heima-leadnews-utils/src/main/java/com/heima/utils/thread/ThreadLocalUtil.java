package com.heima.utils.thread;

/**
 * @Description
 * @Author 嘉然今天吃向晚
 * @Date 2024/12/26-20:18:25
 */
public class ThreadLocalUtil {

    private final static ThreadLocal<Object> THREAD_LOCAL = new ThreadLocal<>();

    public static void setObject(Object obj) {
        THREAD_LOCAL.set(obj);
    }

    public static Object getObject() {
        return THREAD_LOCAL.get();
    }

    public static void rmObject() {
        THREAD_LOCAL.remove();
    }

}
