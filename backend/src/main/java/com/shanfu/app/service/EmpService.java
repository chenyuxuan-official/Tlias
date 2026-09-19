package com.shanfu.app.service;

import com.shanfu.app.pojo.PageResult;
import com.shanfu.app.pojo.EmpQueryParam;
import com.shanfu.app.pojo.Emp;
import java.util.List;
import com.shanfu.app.pojo.LoginInfo;

public interface EmpService {

    /**
     * 分页查询员工信息
     * @param page 当前页码
     * @param pageSize 每页记录数
     * @return 分页结果
     */
    PageResult<Emp> page(EmpQueryParam empQueryParam);

    /**
     * 保存员工信息
     * @param emp 员工信息
     */
    void save(Emp emp);


    // 批量删除
    void deleteByIds(List<Integer> ids);


    // 根据ID查询员工的详细信息
    Emp getInfo(Integer id);

    /**
     * 更新员工信息
     * @param emp 员工信息
     */
    void update(Emp emp);


    // 老师
    List<Emp> findAll();


    // 登录系统
    LoginInfo login(Emp emp);


    /**
     * 修改密码
     * @param empId 当前登录员工的 ID
     * @param oldPassword 原密码
     * @param newPassword 新密码
     */
    void updatePassword(Integer empId, String oldPassword, String newPassword);
}
