package com.heima.utils.thread;

/**
 * @Description
 * @Author 嘉然今天吃向晚
 * @Date 2024/12/26-20:18:25
 */
public class ThreadLocalUtil {

    private final static ThreadLocal<Object> THREAD_LOCAL = new ThreadLocal<>();

    public static void setUserId(Object obj) {
        THREAD_LOCAL.set(obj);
    }

    public static Integer getUserId() {
        Object id = THREAD_LOCAL.get();
        if (id == null)
            return null;
        else if (id instanceof Integer)
            return (Integer) id;
        else if (id instanceof String)
            return Integer.parseInt((String) id);
        return null;
    }

    public static void rmUserId() {
        THREAD_LOCAL.remove();
    }

}
