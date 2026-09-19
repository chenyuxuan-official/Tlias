package com.shanfu.app.mapper;

import com.shanfu.app.pojo.Student;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;
import com.shanfu.app.pojo.StudentQueryParam;
import org.apache.ibatis.annotations.Param;
import java.util.Map;

@Mapper
public interface StudentMapper  {
    List<Student> list(StudentQueryParam studentQueryParam);
    void deleteById(Integer id);
    void deleteByIds(@Param("ids") List<Integer> ids);
    void insert(Student student);
    Student getById(Integer id);
    void updateById(Student student);
    void updateViolation(@Param("id") Integer id, @Param("violation") Integer violation);

    // 学生学历统计
    List<Map> countStudentDegreeData();

    // 统计每个班级的学员人数
    List<Map<String,Object>> countStudentDataByClazz();
}
