package com.Lijiacheng.service;

import com.Lijiacheng.domain.Orders;
import com.baomidou.mybatisplus.extension.service.IService;

public interface OrderService extends IService<Orders> {
    public void submit(Orders orders);
}
