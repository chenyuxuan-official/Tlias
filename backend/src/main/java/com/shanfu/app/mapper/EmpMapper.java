package com.shanfu.app.mapper;


import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import com.shanfu.app.pojo.Emp;
import com.shanfu.app.pojo.EmpQueryParam;
import java.util.Map;

@Mapper
public interface EmpMapper {

    List<Emp> list(EmpQueryParam empQueryParam);

    // 新增员工数据
  @Options(useGeneratedKeys = true, keyProperty = "id")
  @Insert("insert into emp(username, name, gender, phone, job, salary, image, entry_date, dept_id, create_time, update_time) " +
        "values (#{username},#{name},#{gender},#{phone},#{job},#{salary},#{image},#{entryDate},#{deptId},#{createTime},#{updateTime})")
    int insert(Emp emp);


    void deleteByIds(List<Integer> ids);

    // 根据Id查询员工详细信息
    Emp getById(Integer id);

    // 更新员工基本信息
    void updateById(Emp emp);

    // 统计各个职位的员工人数
    List<Map<String,Object>> countEmpJobData();

    // 统计员工性别信息
    List<Map> countEmpGenderData();


    // 老师
    List<Emp> findAll();

    // 根据部门ID统计该部门下的员工数量
    @Select("select count(*) from emp where dept_id = #{deptId}")
    int countByDeptId(Integer deptId);

    // 根据用户名和密码查询员工信息
    @Select("select * from emp where username = #{username} and password = #{password}")
    Emp getUsernameAndPassword(Emp emp);

    // 修改密码（方法有多个参数时必须用 @Param 指定名字，否则 SQL 里的 #{id} 取不到值）
    @Update("update emp set password = #{password}, update_time = now() where id = #{id}")
    void updatePassword(@Param("id") Integer id, @Param("password") String password);
}
