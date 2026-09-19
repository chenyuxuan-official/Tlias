package com.shanfu.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import com.shanfu.app.pojo.PageResult;
import com.shanfu.app.pojo.PasswordDTO;
import com.shanfu.app.pojo.Result;
import com.shanfu.app.anno.Log;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;
import com.shanfu.app.pojo.Emp;
import org.springframework.web.bind.annotation.PutMapping;

import lombok.extern.slf4j.Slf4j;
import com.shanfu.app.service.EmpService;
import com.shanfu.app.pojo.EmpQueryParam;
import org.springframework.web.bind.annotation.PostMapping;
import java.util.List;
import org.springframework.web.bind.annotation.PathVariable;
import com.shanfu.app.utils.CurrentHolder;

@Slf4j
@RestController
@RequestMapping("/emps")
public class EmpController {
     
    @Autowired
    private EmpService empService;
    

    // 分页查询
    @GetMapping
    public Result page(EmpQueryParam empQueryParam) {
        log.info("查询请求参数: {}", empQueryParam);
        PageResult<Emp> pageResult = empService.page(empQueryParam);
        return Result.success(pageResult);
    } // 这里用泛型的原因是 要查询员工和教师，不同的类型

    @Log
    @PostMapping
    public Result save(@RequestBody(required = false) Emp emp){
        log.info("请求参数emp: {}", emp);
        if (emp == null) {
            return Result.error("请求体不能为空");
        }
        empService.save(emp);
        return Result.success();
    }

    // 批量删除员工
    @Log
    @DeleteMapping
    public Result delete(@RequestParam List<Integer> ids){
        log.info("批量删除部门: id = {}", ids);
        empService.deleteByIds(ids);
        return Result.success();
    }

    
    @GetMapping("/{id}")
    public Result getInfo(@PathVariable Integer id){
        log.info("根据id,查询员工的详细信息: id = {}", id);
        Emp emp = empService.getInfo(id);
        return Result.success(emp);
    }

    // 修改员工数据
    @Log
    @PutMapping
    public Result update(@RequestBody Emp emp){
        log.info("修改员工信息,{}",emp);
        empService.update(emp);
        return Result.success();
    }

    // 修改密码（当前登录用户从 token 里取，不信任前端传的 id）
    @PutMapping("/password")
    public Result updatePassword(@RequestBody PasswordDTO passwordDTO){
        Integer empId = CurrentHolder.getCurrentId();
        log.info("修改密码, empId = {}", empId);
        empService.updatePassword(empId, passwordDTO.getOldPassword(), passwordDTO.getNewPassword());
        return Result.success();
    }
}
