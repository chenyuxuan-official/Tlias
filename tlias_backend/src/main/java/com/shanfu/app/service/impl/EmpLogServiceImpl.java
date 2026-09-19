package com.shanfu.app.service.impl;

import com.shanfu.app.mapper.EmpLogMapper;
import com.shanfu.app.pojo.EmpLog;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.shanfu.app.service.EmpLogService;

@Service
public class EmpLogServiceImpl implements EmpLogService {
    @Autowired
    private EmpLogMapper empLogMapper;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void insertLog(EmpLog empLog){
        empLogMapper.insert(empLog);
    }
}
