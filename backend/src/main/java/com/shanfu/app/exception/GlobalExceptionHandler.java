package com.shanfu.app.exception;

import com.shanfu.app.pojo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理请求体 JSON 解析失败的异常
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Result handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.error("请求体解析失败: {}", e.getMessage());
        return Result.error("请求体格式错误，请检查 JSON 格式和字段类型");
    }

    /**
     * 处理参数绑定异常（例如 @DateTimeFormat 格式不对）
     */
    @ExceptionHandler(BindException.class)
    public Result handleBindException(BindException e) {
        log.error("参数绑定失败: {}", e.getMessage());
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ":" + error.getDefaultMessage())
                .findFirst()
                .orElse("参数绑定失败");
        return Result.error(msg);
    }

    /**
     * 处理"部门下还有员工，不允许删除"的业务异常
     */
    @ExceptionHandler(DeptHasEmpException.class)
    public Result handleDeptHasEmpException(DeptHasEmpException e) {
        log.warn("部门删除被拒绝: {}", e.getMessage());
        return Result.error(e.getMessage());
    }

    /**
     * 处理数据库唯一约束冲突（重复的用户名、手机号、学号、身份证号等）
     */
    @ExceptionHandler(DuplicateKeyException.class)
    public Result handleDuplicateKeyException(DuplicateKeyException e) {
        String msg = e.getMessage() == null ? "" : e.getMessage();
        log.warn("唯一约束冲突: {}", msg);

        // MySQL 报错形如：Duplicate entry 'zhangsan' for key 'emp.username'，取 "for key" 之后的索引名
        String key = msg;
        int idx = msg.indexOf("for key '");
        if (idx >= 0) {
            key = msg.substring(idx + "for key '".length()).replace("'", "").trim();
        }

        if (key.contains("username")) {
            return Result.error("用户名已存在，请换一个");
        }
        if (key.contains("id_card")) {
            return Result.error("身份证号已存在");
        }
        if (key.contains("phone")) {
            return Result.error("手机号已存在");
        }
        // 用结尾匹配，不能直接用 contains("no")：那会命中 not、cannot、Unknown 等无关单词
        if (key.equals("no") || key.endsWith(".no")) {
            return Result.error("学号已存在");
        }
        if (key.endsWith(".name")) {
            return key.startsWith("dept") ? Result.error("部门名称已存在") : Result.error("班级名称已存在");
        }
        return Result.error("数据重复，请检查后重试");
    }

    /**
     * 处理修改密码的业务异常（原密码不正确、员工不存在）
     * 注意：PasswordErrorException 与本类同包，不需要 import
     */
    @ExceptionHandler(PasswordErrorException.class)
    public Result handlePasswordErrorException(PasswordErrorException e) {
        log.warn("修改密码失败: {}", e.getMessage());
        return Result.error(e.getMessage());
    }

    /**
     * 处理"请求的接口不存在"
     * Spring Boot 3.2+ 对没匹配到任何 handler 的请求抛的是 NoResourceFoundException，
     * 它继承自 Exception，不单独处理就会被下面的兜底分支吞成"系统繁忙"，不利于排查问题
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public Result handleNoResourceFoundException(NoResourceFoundException e) {
        log.warn("请求的接口不存在: {}", e.getResourcePath());
        return Result.error("请求的接口不存在：" + e.getResourcePath());
    }

    /**
     * 兜底异常处理
     */
    @ExceptionHandler(Exception.class)
    public Result handleException(Exception e) {
        log.error("系统异常: {}", e.getMessage(), e);
        return Result.error("系统繁忙，请稍后再试");
    }
}
