package com.shanfu.app.service;

import com.shanfu.app.pojo.ClazzQueryParam;
import com.shanfu.app.pojo.PageResult;
import com.shanfu.app.pojo.Clazz;
import java.util.List;

public interface ClazzService  {
    // 分页查询的班级--- get
    PageResult<Clazz> page(ClazzQueryParam clazzQueryParam);
    // 删除班级
    void deleteById(Integer id);
    // 添加班级
    void save(Clazz clazz);
    // 根据Id查询
    Clazz getById(Integer id);
    // 修改班级
    void update(Clazz clazz);
    // 查询所有班级
    List<Clazz> findAll();
}