package com.shanfu.app.service.impl;

import com.shanfu.app.pojo.JobOption;
import com.shanfu.app.service.ReportService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.shanfu.app.mapper.EmpMapper;
import com.shanfu.app.service.EmpService;
import com.shanfu.app.mapper.StudentMapper;
import com.shanfu.app.pojo.StudentCountOption;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {
    
    @Autowired
    private EmpMapper empMapper;
    @Autowired
    private StudentMapper studentMapper;

    @Override
    public JobOption getEmpJobData(){
        List<Map<String,Object>> list = empMapper.countEmpJobData();
        List<Object> jobList = list.stream().map(dataMap-> dataMap.get("pos")).toList();
        List<Object> dataList = list.stream().map(dataMap-> dataMap.get("total")).toList();
        return new JobOption(jobList,dataList);
    }


    @Override
    public List<Map> getEmpGenderData(){
        return empMapper.countEmpGenderData();
    }

    @Override
    public List<Map> getStudentDegreeData(){
        return studentMapper.countStudentDegreeData();
    }

    @Override 
    public StudentCountOption getStudentCountData(){
        List<Map<String, Object>> list = studentMapper.countStudentDataByClazz();
        List<Object> clazzList = list.stream().map(m -> m.get("clazzName")).toList();
        List<Object> dataList = list.stream().map(m -> m.get("num")).toList();
        return new StudentCountOption(clazzList,dataList);
    }
}
