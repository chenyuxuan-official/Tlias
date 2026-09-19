package com.shanfu.app.exception;

/**
 * 修改密码时的业务异常（原密码错误、员工不存在等）
 */
public class PasswordErrorException extends RuntimeException {

    public PasswordErrorException(String message) {
        super(message);
    }
}
