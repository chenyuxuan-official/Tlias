package com.shanfu.app.utils;

/**
 * 当前登录用户 id 的持有者
 * <p>
 * 底层基于 ThreadLocal：每个请求线程各存一份，互不干扰。
 * 由 TokenInterceptor 在令牌校验通过后存入，请求结束时清理。
 */
public class CurrentHolder {

    private static final ThreadLocal<Integer> CURRENT_LOCAL = new ThreadLocal<>();

    public static void setCurrentId(Integer employeeId) {
        CURRENT_LOCAL.set(employeeId);
    }

    public static Integer getCurrentId() {
        return CURRENT_LOCAL.get();
    }

    /**
     * 清理 ThreadLocal，避免线程池复用线程时读到上一个请求的残留数据
     */
    public static void remove() {
        CURRENT_LOCAL.remove();
    }
    
}
