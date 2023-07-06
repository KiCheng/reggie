package com.Lijiacheng.controller;

import com.Lijiacheng.common.BaseContext;
import com.Lijiacheng.common.Result;
import com.Lijiacheng.domain.ShoppingCart;
import com.Lijiacheng.service.ShoppingCartService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/shoppingCart")
@Slf4j
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 向购物车中新增菜品或套餐
     * @param shoppingCart
     * @return
     */
    @RequestMapping("/add")
    public Result<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart){

        // 获取当前用户的id，指定是哪个用户的购物车数据
        Long currentId = BaseContext.getCurrentId();
        shoppingCart.setUserId(currentId);

        Long dishId = shoppingCart.getDishId();
        // 菜品和套餐的区别是发送的请求参数一个是dishId一个是setmealId
        // SQL: select * from shopping_cart where user_id = ? and [dish_id = ? 或者 setmeal_id = ?]
        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ShoppingCart::getUserId, currentId);
        if(dishId == null){
            // 加入购物车的是套餐setmealId
            lambdaQueryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }else{
            // 加入购物车的是单菜品
            lambdaQueryWrapper.eq(ShoppingCart::getDishId, shoppingCart.getDishId());
        }

        //  shopping_cart中的一行数据
        ShoppingCart shoppingCartOne = shoppingCartService.getOne(lambdaQueryWrapper);

        // 还要检查加入购物车菜品或者套餐的数量 -- 如果是第一次添加则setNumber=1，并执行save操作；如果不是第一次添加则updateNumber++
        if(shoppingCartOne == null){
            // 第一次添加
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
            shoppingCartOne = shoppingCart;
        }else{
            // 不是第一次添加
            Integer number = shoppingCartOne.getNumber();
            shoppingCartOne.setNumber(++number);
            shoppingCartService.updateById(shoppingCartOne);
        }
        return Result.success(shoppingCartOne);
    }

    /**
     * 向购物车中减少菜品或套餐
     * @param shoppingCart
     * @return
     */
    @RequestMapping("/sub")
    public Result<ShoppingCart> sub(@RequestBody ShoppingCart shoppingCart){
        Long userId = BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);

        // 实际请求参数只有dishId或者setmealId
        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ShoppingCart::getUserId, userId);
        if(shoppingCart.getDishId() != null){
            // 菜品
            lambdaQueryWrapper.eq(ShoppingCart::getDishId, shoppingCart.getDishId());
        } else{
            // 套餐
            lambdaQueryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }
        ShoppingCart one = shoppingCartService.getOne(lambdaQueryWrapper);

        if(one.getNumber() == 1){
            shoppingCartService.remove(lambdaQueryWrapper);
        } else if(one.getNumber() > 1){
            one.setNumber(one.getNumber() - 1);
            shoppingCartService.updateById(one);
        }
        return Result.success(one);
    }



    /**
     * 根据登录用户的id查询购物车的列表清单
     * @return
     */
    @GetMapping("/list")
    public Result<List<ShoppingCart>> list(){

        Long currentId = BaseContext.getCurrentId();

        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ShoppingCart::getUserId, currentId);
        lambdaQueryWrapper.orderByAsc(ShoppingCart::getCreateTime);

        List<ShoppingCart> list = shoppingCartService.list(lambdaQueryWrapper);
        return Result.success(list);
    }


    /**
     * 清空购物车
     * @return
     */
    @DeleteMapping("/clean")
    public Result<String> cleanShoppingCart(){
        Long currentId = BaseContext.getCurrentId();

        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ShoppingCart::getUserId, currentId);
        shoppingCartService.remove(lambdaQueryWrapper);
        return Result.success("清空购物车成功");
    }

}
