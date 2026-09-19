package com.shanfu.app.mapper;

import com.shanfu.app.pojo.LogQueryParam;
import com.shanfu.app.pojo.OperateLog;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface LogMapper {

    // 查询操作日志（关联 emp 表取出操作人姓名）
    List<OperateLog> list(LogQueryParam logQueryParam);
}
