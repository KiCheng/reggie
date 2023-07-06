package com.Lijiacheng.controller;

import com.Lijiacheng.common.BaseContext;
import com.Lijiacheng.common.Result;
import com.Lijiacheng.domain.OrderDetail;
import com.Lijiacheng.domain.Orders;
import com.Lijiacheng.dto.OrderDto;
import com.Lijiacheng.service.OrderDetailService;
import com.Lijiacheng.service.OrderService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderDetailService orderDetailService;

    /**
     * 订单的分页查询(后台)
     * @param page
     * @param pageSize
     * @param number
     * @param beginTime
     * @param endTime
     * @return
     */
    @GetMapping("/page")
    public Result<Page> pageSelect(int page, int pageSize, String number,String beginTime, String endTime){
        Page<Orders> pageInfo = new Page<>(page ,pageSize);
        LambdaQueryWrapper<Orders> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(StringUtils.hasText(number), Orders::getNumber, number);
        lambdaQueryWrapper.gt(StringUtils.hasText(beginTime), Orders::getOrderTime, beginTime)
                .lt(StringUtils.hasText(endTime), Orders::getOrderTime, endTime);
        lambdaQueryWrapper.orderByDesc(Orders::getOrderTime);

        orderService.page(pageInfo, lambdaQueryWrapper);


        return Result.success(pageInfo);
    }


    /**
     * 用户下单
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public Result<String> submit(@RequestBody Orders orders){

        orderService.submit(orders);
        return Result.success("下单成功");
    }

    /**
     * 更改订单的配送状态：请求参数是订单号number和状态status
     * @param orders
     * @return
     */
    @PutMapping
    public Result<String> editStatus(@RequestBody Orders orders){
        Long id = orders.getId();// 订单号
        Integer status = orders.getStatus();  // 配送状态

        LambdaUpdateWrapper<Orders> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(Orders::getId, id);
        lambdaUpdateWrapper.set(Orders::getStatus, status);

        orderService.update(lambdaUpdateWrapper);
        return Result.success("配送状态修改成功");
    }

    /**
     * 订单分页查询（前端）--只能查询该用户的订单
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/userPage")
    public Result<Page> pageDisplay(int page, int pageSize){
        Long userId = BaseContext.getCurrentId();

        Page<Orders> pageInfo = new Page<>(page, pageSize);
        LambdaQueryWrapper<Orders> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Orders::getUserId, userId);
        lambdaQueryWrapper.orderByDesc(Orders::getOrderTime);
        orderService.page(pageInfo, lambdaQueryWrapper);

        Page<OrderDto> dtoPageInfo = new Page<>();
        BeanUtils.copyProperties(pageInfo, dtoPageInfo, "records");

        // 手动给dto中的orderDetails列表属性赋值
        List<Orders> records = pageInfo.getRecords();
        List<OrderDto> orderDtoList = records.stream().map((item) -> {
            OrderDto orderDto = new OrderDto();
            BeanUtils.copyProperties(item, orderDto);

            // orderDetails列表属性赋值
            LambdaQueryWrapper<OrderDetail> detailLambdaQueryWrapper = new LambdaQueryWrapper<>();
            detailLambdaQueryWrapper.eq(OrderDetail::getOrderId, orderDto.getId());
            List<OrderDetail> orderDetails = orderDetailService.list(detailLambdaQueryWrapper);
            orderDto.setOrderDetails(orderDetails);
            return orderDto;
        }).collect(Collectors.toList());

        dtoPageInfo.setRecords(orderDtoList);

        return Result.success(dtoPageInfo);
    }

    /**
     * 根据订单号，回显数据
     * @param orders
     * @return
     */
    @PostMapping("/again")
    public Result<List<OrderDetail>> submitAgain(@RequestBody Orders orders){
        Long orderId = orders.getId();  // 订单号
        LambdaQueryWrapper<OrderDetail> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(OrderDetail::getOrderId, orderId);

        List<OrderDetail> orderDetailList = orderDetailService.list(lambdaQueryWrapper);
        return Result.success(orderDetailList);
    }

}
