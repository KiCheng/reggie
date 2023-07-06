package com.Lijiacheng.service.impl;

import com.Lijiacheng.domain.Category;
import com.Lijiacheng.domain.Dish;
import com.Lijiacheng.domain.Setmeal;
import com.Lijiacheng.exception.ServiceException;
import com.Lijiacheng.mapper.CategoryMapper;
import com.Lijiacheng.service.CategoryService;
import com.Lijiacheng.service.DishService;
import com.Lijiacheng.service.SetmealService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;

    @Override
    public void remove(Long categoryId) {
        // 查询分类是否关联了菜品或者套餐
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(Dish::getCategoryId, categoryId);
        int count1 = (int) dishService.count(dishLambdaQueryWrapper);  // select count(*) from dish where categoryId = ?
        if(count1 > 0){
            // 关联了菜品，无法在分类管理中删除，抛出一个业务异常
            throw new ServiceException("当前分类项关联了菜品，无法删除");
        }

        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId, categoryId);
        int count2 = (int) setmealService.count(setmealLambdaQueryWrapper);  // select count(*) from dish where categoryId = ?
        if(count2 > 0){
            // 关联了菜品，无法在分类管理中删除，抛出一个业务异常
//            log.error("关联了套餐，无法在分类管理中删除，抛出一个业务异常");
            throw new ServiceException("当前分类项关联了套餐，无法删除");
        }
        // 进行删除操作
//        int rows = categoryMapper.deleteById(categoryId);
        super.removeById(categoryId);

    }


}
