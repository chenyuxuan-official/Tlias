package com.shanfu.app.pojo;

import lombok.Data;

/**
 * 修改密码的请求体
 */
@Data
public class PasswordDTO {
    private String oldPassword; // 原密码
    private String newPassword; // 新密码
}
