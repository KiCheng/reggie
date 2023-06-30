package com.Lijiacheng.service.impl;

import com.Lijiacheng.common.BaseContext;
import com.Lijiacheng.domain.*;
import com.Lijiacheng.exception.ServiceException;
import com.Lijiacheng.mapper.OrderMapper;
import com.Lijiacheng.service.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Orders> implements OrderService {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private UserService userService;

    @Autowired
    private AddressBookService addressBookService;

    @Autowired
    private OrderDetailService orderDetailService;

    /**
     * 用户下单提交
     * @param orders
     */
    @Override
    public void submit(Orders orders) {
        // 获得当前用户的id
        Long currentId = BaseContext.getCurrentId();

        // 查询当前用户id的购物车数据
        LambdaQueryWrapper<ShoppingCart> cartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        cartLambdaQueryWrapper.eq(ShoppingCart::getUserId, currentId);
        List<ShoppingCart> cartList = shoppingCartService.list(cartLambdaQueryWrapper);

        if (cartList == null || cartList.size() == 0){
            throw new ServiceException("购物车为空，无法下单");
        }

        // 查询用户数据
        User user = userService.getById(currentId);
        // 查询地址数据
        Long addressBookId = orders.getAddressBookId();
        AddressBook addressBook = addressBookService.getById(addressBookId);

        long orderId = IdWorker.getId();//订单号

        AtomicInteger amount = new AtomicInteger(0);

        // 遍历购物车数据得到订单明细列表数据
        List<OrderDetail> orderDetails = cartList.stream().map((item) -> {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderId);
            orderDetail.setNumber(item.getNumber());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setDishId(item.getDishId());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setName(item.getName());
            orderDetail.setImage(item.getImage());
            orderDetail.setAmount(item.getAmount());
            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
            return orderDetail;
        }).collect(Collectors.toList());

        // 订单表数据设置
        orders.setId(orderId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setStatus(2);
        orders.setAmount(new BigDecimal(amount.get()));//总金额
        orders.setUserId(currentId);
        orders.setNumber(String.valueOf(orderId));
        orders.setUserName(user.getName());
        orders.setConsignee(addressBook.getConsignee());
        orders.setPhone(addressBook.getPhone());
        orders.setAddress((addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
                + (addressBook.getCityName() == null ? "" : addressBook.getCityName())
                + (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
                + (addressBook.getDetail() == null ? "" : addressBook.getDetail()));
        // 向订单表orders插入(1条)数据
        this.save(orders);

        // 向订单明细表order_detail插入(多条)数据
        orderDetailService.saveBatch(orderDetails);

        // 清空购物车数据
        shoppingCartService.remove(cartLambdaQueryWrapper);
    }
}
