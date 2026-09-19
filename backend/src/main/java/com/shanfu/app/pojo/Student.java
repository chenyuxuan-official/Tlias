package com.shanfu.app.pojo;

import java.time.LocalDate;
import lombok.Data;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Student {
    private Integer id; // ID
    private String name; // 姓名
    private String no; // 序号
    private Integer gender; // 性别, 1: 男, 2: 女
    private String idCard; // 身份证号
    private String phone; // 手机号
    private Integer isCollege; // 是否来自于院校, 1: 是, 0: 否
    private String address; // 联系地址
    private Integer degree; // 最高学历, 1: 初中 2 : 高中, 3:大专 4: 本科, 5:硕士, 6: 博士
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate graduationDate; // 毕业时间
    private Integer clazzId; // 班级id
    private Short violationCount; // 违纪次数
    private Short violationScore; // 违纪扣分
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime; // 创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime; // 更新时间


    private String clazzName; // 班级名称
}
