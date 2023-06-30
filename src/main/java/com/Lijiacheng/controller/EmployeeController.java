package com.Lijiacheng.controller;

import com.Lijiacheng.common.BaseContext;
import com.Lijiacheng.common.Result;
import com.Lijiacheng.domain.Employee;
import com.Lijiacheng.service.EmployeeService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    /**
     * 登录功能
     * */
    @PostMapping("/login")
    public Result<Employee> login(HttpServletRequest request, @RequestBody Employee employee){

        // 先判断前端传的值是否是空，如果是空就不需要查表了，节约系统性能
        String username = employee.getUsername();
        String password = employee.getPassword();
        if(username == null || password == null){
            return Result.error("登录失败");
        }

        // 1、将页面提交的密码 password 进行md5加密处理
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        // 2、根据页面提交的用户名 username 查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, username);  // 设置查询条件
        Employee emp = employeeService.getOne(queryWrapper);  // 执行查询操作

        // 3、如果没有查询到用户名则返回登录失败结果
        if(emp == null){
            return Result.error("登录失败");
        }

        // 4、密码比对，如果不一致则返回登录失败结果
        if(!emp.getPassword().equals(password)){
            return Result.error("登录失败");
        }

        // 5、查看员工状态，如果为已禁用状态，则返回员工已禁用结果
        if(emp.getStatus() == 0){
            return Result.error("账号已禁用");
        }

        // 6、登陆成功，将员工id存入Session并返回登录成功
        request.getSession().setAttribute("employee",emp.getId());

        return Result.success(emp);  // 响应回给前端emp对象是因为前端的处理要求
    }

    /**
     * 登出功能
     * */
    @PostMapping("/logout")
    public Result<String> logout(HttpServletRequest request){
        request.getSession().removeAttribute("employee");
        return Result.success("退出成功");
    }

    /**
     * 添加员工
     */
    @PostMapping
    public Result<String> save(HttpServletRequest request, @RequestBody Employee employee){
        // 设置初始密码为123456
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

/*
        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());

        // 获取当前用户的id值
        Long id = (Long)request.getSession().getAttribute("employee");
        employee.setCreateUser(id);
        employee.setUpdateUser(id);
 */
        employeeService.save(employee);

        return Result.success("添加成功");
    }

    /**
     * 员工的分页查询
     * */
    // 来自客户端的请求格式是"http://localhost:8080/employee/page?page=1&pageSize=10&name=123"，这不是RESTful风格的请求，所以不用在getmapping中传参数
    @GetMapping("/page")
    public Result<Page> pageSelect(Integer page, Integer pageSize, String name){
//        log.info("page: {}, pageSize: {}, name: {}", page, pageSize, name);

        // 构造分页构造器
        IPage pageInfo = new Page(page, pageSize);

        // 构造条件构造器
        LambdaQueryWrapper<Employee> lambdaQueryWrapper = new LambdaQueryWrapper<>();
//        lambdaQueryWrapper.like(name != null, Employee::getName, name);
        // 添加过滤条件
        lambdaQueryWrapper.like(StringUtils.hasText(name), Employee::getName, name);
        // 添加排序条件
        lambdaQueryWrapper.orderByDesc(Employee::getUpdateTime);

        // 执行查询
//        pageInfo = employeeService.page(pageInfo, lambdaQueryWrapper);
        employeeService.page(pageInfo, lambdaQueryWrapper);  // 都可以，因为IService的page函数会将查询数据自动封装到page当中
        return Result.success((Page)pageInfo);
    }

    /**
     * 根据id修改员工信息
     * */
    @PutMapping
    public Result<String> update(HttpServletRequest request, @RequestBody Employee employee){
//        log.info(employee.toString());

/*
        employee.setUpdateTime(LocalDateTime.now());

        Long empId = (Long)request.getSession().getAttribute("employee");
        employee.setUpdateUser(empId);
 */

        /**
         * employee.id 通过前端js已经丢失了精度，导致id不准确，无法从数据库中找到相应id的数据  --> 解决方案是拓展mvc的消息转换器(将java对象序列化为要求格式的json数据)
         * */
        employeeService.updateById(employee);
        return Result.success("员工信息修改成功");
    }

    /**
     * 修改员工信息: 回显数据
     * */
    @GetMapping("/{id}")
    public Result<Employee> getById(@PathVariable Long id){
        LambdaQueryWrapper<Employee> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Employee::getId, id);
        Employee emp = employeeService.getOne(lambdaQueryWrapper);

        return Result.success(emp);
    }

}
