package com.shanfu.app.pojo;

import lombok.Data;

@Data
public class StudentQueryParam  {
    private String name; // 姓名模糊
    private Integer degree; // 学历编号
    private Integer clazzId; // 班级Id
    private Integer page = 1;
    private Integer pageSize = 10;
}
