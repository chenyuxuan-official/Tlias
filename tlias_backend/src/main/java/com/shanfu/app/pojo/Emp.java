package com.shanfu.app.pojo;

import java.util.List;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

@Data
public class Emp {
    private Integer id; //ID,主键
    private String username; //用户名
    private String password; //密码
    private String name; //姓名
    private Integer gender; //性别, 1:男, 2:女
    private String phone; //手机号
    private Integer job; //职位, 1:班主任,2:讲师,3:学工主管,4:教研主管,5:咨询师
    private Integer salary; //薪资
    private String image; //头像
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate entryDate; //入职日期
    private Integer deptId; //关联的部门ID
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime; //创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime; //修改时间

    private String deptName; // 返回的时候要把后端数据部门返回回去
    private List<EmpExpr> exprList; // 封装员工工作的经历信息
}
