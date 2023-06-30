package com.Lijiacheng.service.impl;

import com.Lijiacheng.domain.User;
import com.Lijiacheng.mapper.UserMapper;
import com.Lijiacheng.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService{
}
