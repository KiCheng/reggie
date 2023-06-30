package com.Lijiacheng.service.impl;

import com.Lijiacheng.domain.OrderDetail;
import com.Lijiacheng.mapper.OrderDetailMapper;
import com.Lijiacheng.service.OrderDetailService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
}
