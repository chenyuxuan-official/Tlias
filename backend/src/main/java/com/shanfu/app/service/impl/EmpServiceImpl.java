package com.shanfu.app.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.shanfu.app.mapper.EmpMapper;
import com.shanfu.app.pojo.EmpQueryParam;
import  java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.transaction.annotation.Transactional;
import com.shanfu.app.service.EmpLogService;
import com.shanfu.app.pojo.EmpLog;

import com.shanfu.app.pojo.Emp;
import com.shanfu.app.pojo.LoginInfo;
import com.shanfu.app.service.EmpService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.shanfu.app.mapper.EmpExprMapper;
import com.shanfu.app.pojo.PageResult;
import com.shanfu.app.pojo.EmpExpr;
import org.springframework.util.CollectionUtils;
import java.util.List;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import com.shanfu.app.utils.JwtUtils;
import com.shanfu.app.exception.PasswordErrorException;



@Service
public class EmpServiceImpl implements EmpService {
    @Autowired
    private EmpMapper empMapper;

    @Autowired
    private EmpExprMapper empExprMapper;

    @Autowired
    private EmpLogService empLogService;

    @Override
    public PageResult<Emp> page(EmpQueryParam empQueryParam){
        // 开启分页：只对下一次查询生效
        PageHelper.startPage(empQueryParam.getPage(), empQueryParam.getPageSize());
        List<Emp> empList = empMapper.list(empQueryParam);
        // 用 PageInfo 包装结果，自动包含 total、pages 等分页信息
        PageInfo<Emp> pageInfo = new PageInfo<>(empList);
        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }
    
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void save(Emp emp){
       try{
        // 1.补全基础属性
        emp.setCreateTime(LocalDateTime.now());
        emp.setUpdateTime(LocalDateTime.now());

        // 2.保存员工基本信息
        empMapper.insert(emp);

        // 3. 保存员工的工作经历信息 - 批量
        Integer empId = emp.getId();
        List<EmpExpr> exprList = emp.getExprList();
        if (!CollectionUtils.isEmpty(exprList)) {
            exprList.forEach(expr -> expr.setEmpId(empId));
            empExprMapper.insertBatch(exprList);
        }
    } finally{
        // 记录操作日志
        EmpLog empLog = new EmpLog(null, LocalDateTime.now(),emp.toString());
        empLogService.insertLog(empLog);
    } 
    }

    @Transactional
    @Override
    public void deleteByIds(List<Integer> ids){
        empMapper.deleteByIds(ids);

        empExprMapper.deleteByEmpIds(ids);
    }

    @Override
    public Emp getInfo(Integer id){
        return empMapper.getById(id);
    }


    @Transactional
    @Override
    public void update (Emp emp){
        // 1. 根据ID更新员工基本信息
        emp.setUpdateTime(LocalDateTime.now());
        empMapper.updateById(emp);

        // 2. 根据员工ID删除员工的工作经历信息[删除旧的]
        empExprMapper.deleteByEmpIds(Arrays.asList(emp.getId()));

        // 3. 新增员工的工作经历数据 [新增新的]
        Integer empId = emp.getId();
        List<EmpExpr> exprList = emp.getExprList();
        if (!CollectionUtils.isEmpty(exprList)){
            exprList.forEach(expr -> expr.setEmpId(empId));
            empExprMapper.insertBatch(exprList);
        }
    }





    // 老师
    // 1. 查询
    @Override
    public List<Emp> findAll(){
        return empMapper.findAll();
    }

    // 实现login
    @Override
    public LoginInfo login(Emp emp){
        Emp empLogin = empMapper.getUsernameAndPassword(emp);
        if (empLogin != null){
            // 1.生成jwt
            Map<String,Object> dataMap = new HashMap<>();
            dataMap.put("id", empLogin.getId());
            dataMap.put("username", empLogin.getUsername());

            String jwt = JwtUtils.generateJwt(dataMap);
            LoginInfo loginInfo = new LoginInfo(empLogin.getId(), empLogin.getUsername(), empLogin.getName(),jwt);
            return loginInfo;
        }
        return null;
    }

    // 修改密码
    @Override
    public void updatePassword(Integer empId, String oldPassword, String newPassword) {
        // 1. 查出当前登录的员工
        Emp emp = empMapper.getById(empId);
        if (emp == null) {
            throw new PasswordErrorException("员工不存在");
        }

        // 2. 校验原密码是否正确
        if (oldPassword == null || !oldPassword.equals(emp.getPassword())) {
            throw new PasswordErrorException("原密码不正确");
        }

        // 3. 更新为新密码
        empMapper.updatePassword(empId, newPassword);
    }
}
