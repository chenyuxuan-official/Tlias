package com.shanfu.app.mapper;

import com.shanfu.app.pojo.EmpLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Insert;

@Mapper
public interface EmpLogMapper {
    // 插入日志
    @Insert("insert into emp_log(operate_time, info) values (#{operateTime}, #{info})")
    public void insert(EmpLog empLog);
} 
