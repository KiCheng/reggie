package com.Lijiacheng;

import com.Lijiacheng.domain.Employee;
import com.Lijiacheng.mapper.EmployeeMapper;
import com.Lijiacheng.service.EmployeeService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class EmployeeTests {

    @Autowired
    private EmployeeService employeeService;

    @Test
    void testGetByUsername() {
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, "admin");  // 设置查询条件
        Employee emp = employeeService.getOne(queryWrapper);  // 执行查询操作
        System.out.println(emp);
    }

}
