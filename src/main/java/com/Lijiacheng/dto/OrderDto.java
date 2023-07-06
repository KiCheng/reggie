package com.Lijiacheng.dto;

import com.Lijiacheng.domain.OrderDetail;
import com.Lijiacheng.domain.Orders;
import lombok.Data;

import java.util.List;

@Data
public class OrderDto extends Orders{
    private List<OrderDetail> orderDetails;

}
