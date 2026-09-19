package com.shanfu.app.service;


import com.shanfu.app.pojo.PageResult;
import com.shanfu.app.pojo.Student;
import com.shanfu.app.pojo.StudentQueryParam;
import java.util.List;



public interface StudentService {
    // 分页查询
    PageResult<Student> page(StudentQueryParam studentQueryParam);
    // 按照id删除
    void deleteByIds(List<Integer> ids);
    // 保存
    void save(Student student);
    // 按照id查询
    Student getInfo(Integer id);
    
    // 更新
    void update(Student student);
    // 违规处理
    void handleViolation(Integer id, Integer Score);
}
