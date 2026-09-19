package com.shanfu.app.service.impl;

import com.shanfu.app.mapper.StudentMapper;
import com.shanfu.app.pojo.PageResult;
import com.shanfu.app.pojo.Student;
import com.shanfu.app.pojo.StudentQueryParam;
import com.shanfu.app.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import java.util.List;
import java.time.LocalDateTime;

@Service
public class StudentServiceImpl implements StudentService {
    @Autowired
    private StudentMapper studentMapper;

    // 分页处理
    @Override 
    public PageResult<Student> page(StudentQueryParam studentQueryParam) {
        PageHelper.startPage(studentQueryParam.getPage(), studentQueryParam.getPageSize());
        List<Student> studentList = studentMapper.list(studentQueryParam);
        PageInfo<Student> pageInfo = new PageInfo<>(studentList);
        return new PageResult<>(pageInfo.getTotal(), pageInfo.getList());
    }

    // 按照id删除
    @Override
    public void deleteByIds(List<Integer> ids) {
        studentMapper.deleteByIds(ids);
    }

    // 保存
    @Override
    public void save(Student student) {
        // 补全时间
        student.setCreateTime(LocalDateTime.now());
        student.setUpdateTime(LocalDateTime.now());
        studentMapper.insert(student);
    }


    // 根据id查询
    @Override 
    public Student getInfo(Integer id) {
        return studentMapper.getById(id);
    }

    // 更新
    @Override 
    public void update(Student student){
        student.setUpdateTime(LocalDateTime.now());
        studentMapper.updateById(student);
    }

    // 违纪处理
    @Override 
    public void handleViolation(Integer id, Integer score){
        studentMapper.updateViolation(id, score);
    }
    
}
