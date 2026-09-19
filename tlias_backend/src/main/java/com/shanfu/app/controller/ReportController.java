package com.shanfu.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import com.shanfu.app.pojo.Result;
import com.shanfu.app.service.ReportService;
import java.util.List;
import java.util.Map;
import com.shanfu.app.pojo.JobOption;
import org.springframework.web.bind.annotation.RequestParam;
import com.shanfu.app.pojo.StudentCountOption;



@Slf4j
@RequestMapping("/report")
@RestController
public class ReportController {
    
    @Autowired
    private ReportService reportService;

    @GetMapping("/empJobData")
    public Result getEmpJobData(){
        log.info("统计各个职位的员工人数");
        JobOption jobOption = reportService.getEmpJobData();
        return Result.success(jobOption);
    }
     
    // 统计员工性别信息
    @GetMapping("/empGenderData")
    public Result getEmpGenderData(){
        log.info("统计员工性别信息");
        List<Map> genderList = reportService.getEmpGenderData();
        return Result.success(genderList);
    }

    // 学员学历统计
    @GetMapping("/studentDegreeData")
    public Result getStudentDegreeData(){
        log.info("统计学生学历信息");
        List<Map> degreeList = reportService.getStudentDegreeData();
        return Result.success(degreeList);
    }

    // 班级人数统计
    @GetMapping("/studentCountData")
    public Result getStudentCountData() {
        log.info("统计每个班级的人数");
       StudentCountOption studentCountOption = reportService.getStudentCountData();
        return Result.success(studentCountOption);
    }
    
}
