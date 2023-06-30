package com.Lijiacheng.service.impl;

import com.Lijiacheng.domain.ShoppingCart;
import com.Lijiacheng.mapper.ShoppingCartMapper;
import com.Lijiacheng.service.ShoppingCartService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {
}
