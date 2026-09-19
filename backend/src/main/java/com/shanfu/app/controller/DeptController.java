package com.shanfu.app.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import com.shanfu.app.anno.Log;
import com.shanfu.app.service.DeptService;
import com.shanfu.app.pojo.Dept;
import com.shanfu.app.pojo.Result;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@RequestMapping("/depts")
@RestController
public class DeptController {
    @Autowired
    private DeptService deptService; // 多态

    // 查询部门列表
    @GetMapping
    public Result list() {
        List<Dept> deptList = deptService.findAll();
        return Result.success(deptList);
    }

    // 删除部门列表
    @Log
    @DeleteMapping
    public Result delete(Integer id){
        deptService.deleteById(id);
        return Result.success();
    }

    // 增加
    @Log
    @PostMapping
    public Result add(@RequestBody Dept dept){
        log.info("添加部门: {}", dept);
        deptService.add(dept);
        return Result.success();
    }

    // 查
    @GetMapping("/{id}")
    public Result get(@PathVariable Integer id){
        log.info("根据id查询, id = {}", id);
        Dept dept = deptService.getById(id);
        return Result.success(dept);
    }

    // 修改部门数据
    @Log
    @PutMapping
    public Result update(@RequestBody Dept dept){
        log.info("修改部门, dept = {}", dept);
        deptService.update(dept);
        return Result.success();
    }
}
