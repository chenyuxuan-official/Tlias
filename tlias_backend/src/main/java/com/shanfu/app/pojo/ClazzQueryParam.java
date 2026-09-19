package com.shanfu.app.pojo;

import lombok.Data;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

@Data
public class ClazzQueryParam {
    private Integer page = 1; // 页码，默认第 1 页
    private Integer pageSize = 10; // 每页条数，默认 10
    private String name;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate begin;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate end;
    
    
}
