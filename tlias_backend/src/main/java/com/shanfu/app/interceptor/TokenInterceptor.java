package com.shanfu.app.interceptor;

import com.shanfu.app.utils.CurrentHolder;
import com.shanfu.app.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 令牌校验拦截器
 */
@Slf4j
@Component
public class TokenInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1. 获取请求 url
        String url = request.getRequestURL().toString();

        // 2. 判断请求 url 中是否包含 login，如果包含，说明是登录操作，放行
        if (url.contains("login")) {
            log.info("登录请求，直接放行");
            return true;
        }

        // 3. 获取请求头中的令牌（token）
        String jwt = request.getHeader("token");

        // 4. 判断令牌是否存在，如果不存在，返回错误结果（未登录）
        if (!StringUtils.hasLength(jwt)) {
            log.info("获取到 jwt 令牌为空，返回错误结果");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        // 5. 解析 token，如果解析失败，返回错误结果（未登录）
        Claims claims = null;
        try {
            claims = JwtUtils.parseJWT(jwt);
        } catch (Exception e) {
            e.printStackTrace();
            log.info("解析令牌失败，返回错误结果");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        // 6. 解析成功，把当前登录用户的 id 存入 ThreadLocal，供操作日志切面记录操作人
        Object id = claims.get("id");
        if (id != null) {
            CurrentHolder.setCurrentId(Integer.valueOf(id.toString()));
        }

        // 7. 放行
        log.info("令牌合法，放行");
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 请求处理完毕，清理 ThreadLocal，避免线程池复用线程时数据串用
        CurrentHolder.remove();
    }
}
