package com.shanfu.app.aspect;

import com.shanfu.app.mapper.OperateLogMapper;
import com.shanfu.app.pojo.OperateLog;
import com.shanfu.app.utils.CurrentHolder;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * 操作日志切面：记录 controller 包下所有增、删、改接口的操作日志
 */
@Slf4j
@Component
@Aspect // 当前类为切面类
public class OperateLogAspect {

    @Autowired
    private OperateLogMapper operateLogMapper;

    /**
     * 切入点：所有张贴了 @Log 注解的方法（即需要记录操作日志的增、删、改接口）
     */
    @Around("@annotation(com.shanfu.app.anno.Log)")
    public Object recordLog(ProceedingJoinPoint pjp) throws Throwable {
        // 记录方法执行开始时间
        long begin = System.currentTimeMillis();

        Object result = null;
        try {
            // 执行原始方法，并拿到返回值
            result = pjp.proceed();
            return result;
        } finally {
            // 无论业务执行成功还是抛异常，都要记录操作日志
            long end = System.currentTimeMillis();

            // 组装日志对象
            OperateLog operateLog = new OperateLog();
            operateLog.setOperateEmpId(CurrentHolder.getCurrentId());
            operateLog.setOperateTime(LocalDateTime.now());

            MethodSignature signature = (MethodSignature) pjp.getSignature();
            operateLog.setClassName(signature.getDeclaringTypeName()); // 全类名
            operateLog.setMethodName(signature.getName());             // 方法名

            // 方法运行时参数、返回值，截断到数据库字段允许的长度
            operateLog.setMethodParams(truncate(Arrays.toString(pjp.getArgs()), 1000));
            operateLog.setReturnValue(truncate(String.valueOf(result), 2000));
            operateLog.setCostTime(end - begin);

            // 保存日志
            operateLogMapper.insert(operateLog);
            log.info("操作日志已记录: {}.{}，耗时 {}ms",
                    operateLog.getClassName(), operateLog.getMethodName(), operateLog.getCostTime());
        }
    }

    /**
     * 截断超长字符串，避免参数或返回值超出数据库字段长度导致插入失败
     */
    private String truncate(String value, int maxLength) {
        if (value == null || value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength);
    }
}
