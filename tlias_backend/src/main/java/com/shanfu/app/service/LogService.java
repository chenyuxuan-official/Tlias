package com.shanfu.app.service;

import com.shanfu.app.pojo.LogQueryParam;
import com.shanfu.app.pojo.OperateLog;
import com.shanfu.app.pojo.PageResult;

public interface LogService {

    // 分页查询操作日志
    PageResult<OperateLog> page(LogQueryParam logQueryParam);
}
