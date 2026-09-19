package com.shanfu.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.shanfu.app.pojo.StudentQueryParam;
import com.shanfu.app.service.StudentService;
import com.shanfu.app.pojo.PageResult;
import com.shanfu.app.pojo.Result;
import com.shanfu.app.anno.Log;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.shanfu.app.pojo.Student;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PutMapping;

@Slf4j
@RestController
@RequestMapping("/students")
public class StudentController {
    
    @Autowired 
    private StudentService studentService;

    // 学员条件分页列表
    @GetMapping
    public Result page(StudentQueryParam studentQueryParam){
        log.info("学员分页查询, 参数： {}", studentQueryParam);
        PageResult pageResult = studentService.page(studentQueryParam);
        return Result.success(pageResult);
    }

    // 批量删除
    @Log
    @DeleteMapping("/{ids}")
    public Result delete(@PathVariable List<Integer> ids){
        log.info("根据id删除学员, ids; {}", ids);
        studentService.deleteByIds(ids);
        return Result.success();
    }

    // 添加学员
    @Log
    @PostMapping
    public Result save(@RequestBody Student student){
        studentService.save(student);
        return Result.success();
    }

    // 根据id查询
    @GetMapping("/{id}")
    public Result getInfo(@PathVariable Integer id){
        log.info("根据id查询学员, id; {}", id);
        Student student = studentService.getInfo(id);
        return Result.success(student);
    }

    // 更新学员
    @Log
    @PutMapping
    public Result update(@RequestBody Student student){
        log.info("更新学员, {}", student);
        studentService.update(student);
        return Result.success();
    }

    // 违纪处理,自增违纪次数，扣分
    @Log
    @PutMapping("/violation/{id}/{score}")
    public Result violation(@PathVariable Integer id, @PathVariable Integer score){
        log.info("学员违纪处理, id = {}, score = {}", id, score);
        studentService.handleViolation(id,score);
        return Result.success();
    }
}
