package com.shanfu.app.pojo;

import java.time.LocalDate;
import lombok.Data;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class Clazz {
    private Integer id; // ID
    private String name; // 班级名称
    private String room; // 班级教室
    private LocalDate beginDate; // 开课时间
    private LocalDate endDate; // 结课时间
    private Integer masterId; // 班主任
    private Integer subject; // 学科
    private LocalDateTime createTime; // 创建时间
    private LocalDateTime updateTime; // 更新时间

    private String masterName; // 班主任名字
    private String status; // 班级状态- 未开班，在读，已结课
}
