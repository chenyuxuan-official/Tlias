package com.shanfu.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import com.shanfu.app.pojo.Result;
import com.shanfu.app.anno.Log;
import com.shanfu.app.pojo.ClazzQueryParam;
import com.shanfu.app.pojo.PageResult;
import com.shanfu.app.pojo.Clazz;
import com.shanfu.app.service.ClazzService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/clazzs")
public class ClazzsController {
    

    @Autowired 
    private ClazzService clazzService;
    

    // 分页查询
    @GetMapping
    public Result page(ClazzQueryParam clazzQueryParam){
        log.info("班级分页查询，参数: {}", clazzQueryParam);
        PageResult<Clazz> pageResult = clazzService.page(clazzQueryParam);
        return Result.success(pageResult);
    }

    // 根据ID删除
    @Log
    @DeleteMapping("{id}")
    public Result delete(@PathVariable Integer id){
        log.info("删除班级, id = {}", id);
        clazzService.deleteById(id);
        return Result.success();
    }

    // 添加班级
    @Log
    @PostMapping
    public Result sava(@RequestBody Clazz clazz){
        log.info("添加班级, clazz = {}",clazz);
        clazzService.save(clazz);
        return Result.success();
    }

    // 根据Id查询
    @GetMapping("{id}")
    public Result getById(@PathVariable Integer id){
        log.info("根据Id查询班级, id = {}", id);
        Clazz clazz = clazzService.getById(id);
        return Result.success(clazz);
    }

    // 修改班级
    @Log
    @PutMapping
    public Result update(@RequestBody Clazz clazz){
        log.info("修改班级, clazz = {}" , clazz);
        clazzService.update(clazz);
        return Result.success();
    }

    // 查询所有班级
    @GetMapping("/list")
    public Result findAll(){
        log.info("查询所有班级");
        List<Clazz> clazzList = clazzService.findAll();
        return Result.success(clazzList);
    }
}
