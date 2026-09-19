package com.shanfu.app.service;

import com.shanfu.app.pojo.JobOption;
import com.shanfu.app.pojo.StudentCountOption;

import java.util.List;
import java.util.Map;


public interface ReportService {
    /**
     * 统计各个职位的员工人数
     * @return
     */
    JobOption getEmpJobData();

    // 统计员工性别信息
    List<Map> getEmpGenderData();

    // 统计学员学历信息
    List<Map> getStudentDegreeData();
    StudentCountOption getStudentCountData();

}
