package com.shanfu.app.service;

import com.shanfu.app.pojo.EmpLog;
import org.springframework.stereotype.Service;

@Service
public interface EmpLogService {
     // 记录新增员工日志
     public void insertLog(EmpLog empLog);
}
