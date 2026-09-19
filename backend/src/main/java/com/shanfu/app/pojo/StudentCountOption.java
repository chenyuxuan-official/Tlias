package com.shanfu.app.pojo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Data 
@NoArgsConstructor
@AllArgsConstructor
public class StudentCountOption {
    private List clazzList; // 班级名列表（x轴）
    private List dataList; // 人数列表（y轴）
}
