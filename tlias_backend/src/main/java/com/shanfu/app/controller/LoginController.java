package com.shanfu.app.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.shanfu.app.pojo.LoginInfo;
import com.shanfu.app.pojo.Emp;
import com.shanfu.app.service.EmpService;
import com.shanfu.app.pojo.Result;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class LoginController {

    @Autowired
    private EmpService empService;

    @PostMapping("/login")
    public Result login(@RequestBody Emp emp) {
        log.info("员工来登录 , {}", emp);
        LoginInfo loginInfo = empService.login(emp);
        if (loginInfo != null){
             return Result.success(loginInfo);
        }
        return Result.error("用户或者密码错误");
    }
}
