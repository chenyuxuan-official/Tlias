package com.shanfu.app.exception;

// 自定义业务异常：部门下还有员工，不允许删除
public class DeptHasEmpException extends RuntimeException {
    public DeptHasEmpException(String message) {
        super(message);
    }
}
