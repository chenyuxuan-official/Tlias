package com.shanfu.app.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.shanfu.app.mapper.LogMapper;
import com.shanfu.app.pojo.LogQueryParam;
import com.shanfu.app.pojo.OperateLog;
import com.shanfu.app.pojo.PageResult;
import com.shanfu.app.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LogServiceImpl implements LogService {

    @Autowired
    private LogMapper logMapper;

    @Override
    public PageResult<OperateLog> page(LogQueryParam logQueryParam) {
        // 开启分页，只对下一次查询生效
        PageHelper.startPage(logQueryParam.getPage(), logQueryParam.getPageSize());
        List<OperateLog> logList = logMapper.list(logQueryParam);
        PageInfo<OperateLog> pageInfo = new PageInfo<>(logList);
        return new PageResult<>(pageInfo.getTotal(), pageInfo.getList());
    }
}
