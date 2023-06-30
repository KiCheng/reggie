package com.Lijiacheng.service.impl;

import com.Lijiacheng.domain.Employee;
import com.Lijiacheng.mapper.EmployeeMapper;
import com.Lijiacheng.service.EmployeeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {

}
