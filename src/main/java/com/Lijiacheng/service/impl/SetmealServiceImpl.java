package com.Lijiacheng.service.impl;

import com.Lijiacheng.domain.Setmeal;
import com.Lijiacheng.domain.SetmealDish;
import com.Lijiacheng.dto.SetmealDto;
import com.Lijiacheng.exception.ServiceException;
import com.Lijiacheng.mapper.SetmealMapper;
import com.Lijiacheng.service.SetmealDishService;
import com.Lijiacheng.service.SetmealService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;

    /**
     * 新增套餐，同时需要保存套餐和菜品之间的对应关系
     * @param setmealDto
     */
    @Override
    public void saveWithDish(SetmealDto setmealDto) {
        // 保存套餐的基本信息 setmeal表
        this.save(setmealDto);

        // 保存套餐和菜品的关联信息 setmeal_dish表
        List<SetmealDish> setmealDishList = setmealDto.getSetmealDishes();

        // setmeal_id没有赋值，需要手动赋值
        setmealDishList.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        setmealDishService.saveBatch(setmealDishList);
    }

    /**
     * 删除套餐（必须状态为停售status==0），并且同时删除setmeal_dish表中的对应setmealId的数据
     * @param ids
     */
    @Override
    public void removeWithDish(List<Long> ids) {
        // 首先检查状态是否为停售状态 select count(*) from setmeal where id in (选中的ids) and status == 1
        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(Setmeal::getId, ids);
        lambdaQueryWrapper.eq(Setmeal::getStatus, 1); // status==1 在售
        int count = (int) this.count(lambdaQueryWrapper);

        // 如果状态为在售，则不能删除，抛出一个业务异常
        if(count > 0){
            // 批量删除中只要查询到有一个套餐在售，都会操作失败抛出异常
            throw new ServiceException("套餐在售，无法删除");
        }

        // 如果状态为停售，则可以删除，首先操作setmeal表删除套餐
        this.removeByIds(ids);

        // 再操作setmeal_dish表删除setmealId为该套餐的数据行
        LambdaQueryWrapper<SetmealDish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.in(SetmealDish::getSetmealId, ids);
        setmealDishService.remove(dishLambdaQueryWrapper);

    }
}
