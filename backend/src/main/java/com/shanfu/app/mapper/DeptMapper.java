package com.shanfu.app.mapper;


import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import com.shanfu.app.pojo.Dept;
import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Update;
        
@Mapper
public interface DeptMapper  {
    // 查询所有部门
    @Select("select * from dept")
    public List<Dept> findAll();

    // 删除
    @Delete("delete from dept where id = #{id}")
    public void deleteById(Integer id);

    // 增加
    @Insert("insert into dept(name,create_time,update_time) values(#{name},#{createTime},#{updateTime})")
    public void insert(Dept dept);

    // 查
    @Select("select id,name,create_time,update_time from dept where id = #{id}")
    public Dept getById(Integer id);

    // 修改
    @Update("update dept set name = #{name} ,update_time = #{updateTime} where id = #{id}")
    public void update(Dept dept);
}
