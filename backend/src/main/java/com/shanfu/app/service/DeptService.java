package com.shanfu.app.service;

import com.shanfu.app.pojo.Dept;
import java.util.List;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

public interface DeptService {
    public List<Dept> findAll();
    public void deleteById(Integer id);
    public void add(Dept dept);
    public Dept getById(Integer id);
    public void update(Dept dept);
}
