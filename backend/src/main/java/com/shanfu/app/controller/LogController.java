package com.shanfu.app.controller;

import com.shanfu.app.pojo.LogQueryParam;
import com.shanfu.app.pojo.OperateLog;
import com.shanfu.app.pojo.PageResult;
import com.shanfu.app.pojo.Result;
import com.shanfu.app.service.LogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/logs")
public class LogController {

    @Autowired
    private LogService logService;

    // 操作日志分页查询
    @GetMapping
    public Result page(LogQueryParam logQueryParam) {
        log.info("操作日志分页查询, 参数: {}", logQueryParam);
        PageResult<OperateLog> pageResult = logService.page(logQueryParam);
        return Result.success(pageResult);
    }
}
