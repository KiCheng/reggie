package com.Lijiacheng.service.impl;

import com.Lijiacheng.domain.Dish;
import com.Lijiacheng.domain.DishFlavor;
import com.Lijiacheng.dto.DishDto;
import com.Lijiacheng.mapper.DishMapper;
import com.Lijiacheng.service.DishFlavorService;
import com.Lijiacheng.service.DishService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;

    /**
     * 新增菜品，同时添加菜品对应的口味数据
     * @param dishDto
     */
    @Override
    public void saveWithFlavor(DishDto dishDto) {
        // 保存菜品的基本信息到菜品表dish
        this.save(dishDto);

        // 保存菜品口味数据到菜品口味表dish_flavor，要注意dish_id无法通过dto对象赋值
        List<DishFlavor> flavors = dishDto.getFlavors();
        for(DishFlavor flavor: flavors){
            flavor.setDishId(dishDto.getId());
        }
        dishFlavorService.saveBatch(flavors);

    }

    /**
     * 根据id查询菜品信息和对应的口味信息
     * @param dishId
     * @return
     */
    @Override
    public DishDto getByIdWithFlavor(Long dishId) {
        // 查询菜品的基本信息
        Dish dish = this.getById(dishId);

        // 查询菜品的口味信息
        LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(DishFlavor::getDishId, dishId);
        List<DishFlavor> dishFlavors = dishFlavorService.list(lambdaQueryWrapper);

        // 对象拷贝
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish, dishDto);
        dishDto.setFlavors(dishFlavors);

        return dishDto;
    }

    /**
     * 根据dish_id修改菜品信息和菜品口味信息
     * @param dishDto
     */
    @Override
    public void updateWithFlavor(DishDto dishDto) {
        Long dishId = dishDto.getId();
        // 更新菜品基本信息
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(Dish::getId, dishId);
        this.update(dishDto, dishLambdaQueryWrapper);
        // 更新菜品口味信息
        // 1) 清理当前菜品对应的口味数据 -- delete dish_flavor表所有dish_id = #{dish_id}的数据
        LambdaQueryWrapper<DishFlavor> flavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
        flavorLambdaQueryWrapper.eq(DishFlavor::getDishId, dishId);
        dishFlavorService.remove(flavorLambdaQueryWrapper);
        // 2) 重新添加修改后菜品对应的口味数据 -- insert dish_flavor
        List<DishFlavor> flavorList = dishDto.getFlavors();
        for(DishFlavor flavor: flavorList){
            flavor.setDishId(dishId);
        }
        dishFlavorService.saveBatch(flavorList);

    }
}
