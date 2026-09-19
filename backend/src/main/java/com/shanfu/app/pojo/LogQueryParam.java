package com.shanfu.app.pojo;

import lombok.Data;

/**
 * 操作日志查询参数
 */
@Data
public class LogQueryParam {
    private Integer page = 1;       // 页码，默认第 1 页
    private Integer pageSize = 10;  // 每页条数，默认 10
}
