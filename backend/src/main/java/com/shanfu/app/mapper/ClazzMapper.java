package com.shanfu.app.mapper;


import org.apache.ibatis.annotations.Mapper;
import com.shanfu.app.pojo.ClazzQueryParam;
import com.shanfu.app.pojo.Clazz;
import java.util.List;
import org.apache.ibatis.annotations.Insert;

@Mapper
public interface ClazzMapper  {
    // 条件分页查询
    List<Clazz> list(ClazzQueryParam clazzQueryParam);
    // 删除班级
    void deleteById(Integer id);
    // 添加班级
    @Insert("Insert into clazz (name, room, begin_date, end_date, master_id, subject, create_time, update_time) " + 
     "values (#{name}, #{room}, #{beginDate}, #{endDate}, #{masterId}, #{subject}, #{createTime}, #{updateTime})")
    void insert(Clazz clazz);
    // 根据Id查询
    Clazz getById(Integer id);
    // 修改班级
    void updateByPrimaryKey(Clazz clazz);

    // 查询所在班级
    List<Clazz> findAll();
}
