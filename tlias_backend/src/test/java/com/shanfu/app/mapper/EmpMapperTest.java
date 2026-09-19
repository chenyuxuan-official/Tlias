package com.shanfu.app.mapper;

import com.shanfu.app.pojo.Emp;
import com.shanfu.app.pojo.EmpQueryParam;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * EmpMapper 集成测试
 * 依赖真实数据库，通过 @Sql 准备和清理测试数据，@Transactional 保证测试后回滚
 */
@SpringBootTest
@Transactional
@Sql(scripts = "/sql/init-emp-test.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class EmpMapperTest {

    @Autowired
    private EmpMapper empMapper;

    @Test
    void list_shouldReturnAllEmployeesWithDeptName() {
        EmpQueryParam query = new EmpQueryParam();
        List<Emp> empList = empMapper.list(query);

        assertNotNull(empList, "查询结果不应为 null");
        assertEquals(2, empList.size(), "应返回 2 条员工记录");

        Emp first = empList.get(0);
        assertEquals("张三", first.getName());
        assertEquals(1, first.getDeptId());
        assertEquals("研发部", first.getDeptName(), "联查部门名称应正确映射到 deptName");

        Emp second = empList.get(1);
        assertEquals("李四", second.getName());
        assertEquals(2, second.getDeptId());
        assertEquals("市场部", second.getDeptName());
    }

    @Test
    void list_shouldReturnFilteredEmployeesByName() {
        EmpQueryParam query = new EmpQueryParam();
        query.setName("张");
        List<Emp> empList = empMapper.list(query);

        assertNotNull(empList, "查询结果不应为 null");
        assertEquals(1, empList.size(), "按名字模糊查询应只返回 1 条记录");
        assertEquals("张三", empList.get(0).getName());
    }

    @Test
    @Sql(scripts = "/sql/clear-emp-test.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void list_shouldReturnEmptyListWhenNoData() {
        EmpQueryParam query = new EmpQueryParam();
        List<Emp> empList = empMapper.list(query);

        assertNotNull(empList, "查询结果不应为 null");
        assertTrue(empList.isEmpty(), "无数据时应返回空列表");
    }
}
