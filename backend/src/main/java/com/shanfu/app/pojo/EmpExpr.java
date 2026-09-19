package com.shanfu.app.pojo;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

@Data
public class EmpExpr {
    private Integer id;
    private Integer empId;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate begin;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate end;    
    private String company;
    private String job;
}
