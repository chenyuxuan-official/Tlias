package com.shanfu.app.service.impl;

import java.util.List;

import com.shanfu.app.service.DeptService;
import org.springframework.stereotype.Service;
import com.shanfu.app.mapper.DeptMapper;
import com.shanfu.app.mapper.EmpMapper;
import com.shanfu.app.pojo.Dept;
import org.springframework.beans.factory.annotation.Autowired;
import java.time.LocalDateTime;
import com.shanfu.app.exception.DeptHasEmpException;

@Service
public class DeptServiceImpl implements DeptService {
    @Autowired
    private DeptMapper deptMapper;

    @Autowired
    private EmpMapper empMapper;

    @Override
    public List<Dept> findAll() {
        return deptMapper.findAll();
    }

    @Override
    public void deleteById(Integer id){
        // 先判断该部门下是否有员工，如果有，则不允许删除
        Integer count = empMapper.countByDeptId(id);
        if (count != null && count > 0) {
            throw new DeptHasEmpException("对不起，当前部门下有员工，不能直接删除！");
        }
        deptMapper.deleteById(id);
    }

    @Override
    public void add(Dept dept){
        dept.setCreateTime(LocalDateTime.now());
        dept.setUpdateTime(LocalDateTime.now());
        deptMapper.insert(dept);
    }

    @Override
    public Dept getById(Integer id){
        return deptMapper.getById(id);
    }

    @Override
    public void update(Dept dept){
        dept.setUpdateTime(LocalDateTime.now());
        deptMapper.update(dept);
    }
}
