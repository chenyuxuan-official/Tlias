package com.shanfu.app.service.impl;
    
import com.shanfu.app.service.ClazzService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.shanfu.app.pojo.ClazzQueryParam;
import com.shanfu.app.pojo.PageResult;
import com.shanfu.app.pojo.Clazz;
import com.shanfu.app.mapper.ClazzMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ClazzServiceImpl implements ClazzService {
    
    @Autowired 
    private ClazzMapper clazzMapper;

    @Override 
    public PageResult<Clazz> page(ClazzQueryParam clazzQueryParam) {
        // 开启分页
        PageHelper.startPage(clazzQueryParam.getPage(), clazzQueryParam.getPageSize());
        List<Clazz> clazzList = clazzMapper.list(clazzQueryParam);
        PageInfo<Clazz> pageInfo = new PageInfo<>(clazzList);
        // 班级状态由开课/结课日期推导（数据库中没有该字段）
        LocalDate today = LocalDate.now();
        for (Clazz clazz : pageInfo.getList()) {
            if (clazz.getBeginDate() != null && today.isBefore(clazz.getBeginDate())) {
                clazz.setStatus("未开班");
            } else if (clazz.getEndDate() != null && today.isAfter(clazz.getEndDate())) {
                clazz.setStatus("已结课");
            } else {
                clazz.setStatus("在读");
            }
        }
        return new PageResult<>(pageInfo.getTotal(), pageInfo.getList());
    }

    // 删除按照Id
    @Override 
    public void deleteById(Integer id) {
        clazzMapper.deleteById(id);
    }

    // 删除班级,创建时间是系统自己创
    @Override 
    public void save(Clazz clazz){
        // 1. 补全时间
        clazz.setCreateTime(LocalDateTime.now());
        clazz.setUpdateTime(LocalDateTime.now());
        // 2. 插入
        clazzMapper.insert(clazz);
    }

    // 根据Id查询
    @Override 
    public Clazz getById(Integer id) {
        return clazzMapper.getById(id);
    }

    // 修改班级 ,系统自动更新修改时间
    @Override 
    public void update(Clazz clazz) {
        clazz.setUpdateTime(LocalDateTime.now());
        clazzMapper.updateByPrimaryKey(clazz);
    }

    // 查询所有班级
    @Override 
    public List<Clazz> findAll() {
        return clazzMapper.findAll();
    }
}
