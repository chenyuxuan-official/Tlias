package com.shanfu.app.pojo;

import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;
import lombok.Data;

@Data
public class EmpQueryParam {
    
    private Integer page = 1; // 页数
    private Integer pageSize = 10; // 每页展示记录数
    private String name;
    private Integer gender;
    private Integer job; // 职位: 1班主任, 2讲师, 3学工主管, 4教研主管, 5咨询师
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate begin; // 入职开始时间
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate end; // 入职结束时间
}
