package com.Lijiacheng.controller;

import com.Lijiacheng.common.Result;
import com.Lijiacheng.domain.Category;
import com.Lijiacheng.domain.Dish;
import com.Lijiacheng.domain.Setmeal;
import com.Lijiacheng.domain.SetmealDish;
import com.Lijiacheng.dto.SetmealDto;
import com.Lijiacheng.service.CategoryService;
import com.Lijiacheng.service.SetmealDishService;
import com.Lijiacheng.service.SetmealService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.el.util.ReflectionUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/setmeal")
public class SetmealController {
    @Autowired
    private SetmealService setmealService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SetmealDishService setmealDishService;

    /**
     * 新增套餐 （setmeal表新增套餐信息，在setmeal_dish表新增套餐内的菜品信息）
     * @param setmealDto
     * @return
     */
    @PostMapping
    public Result<String> save(@RequestBody SetmealDto setmealDto){

        setmealService.saveWithDish(setmealDto);
        return Result.success("新增套餐成功");
    }

    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public Result<Page> pageSelect(int page, int pageSize, String name){
        Page<Setmeal> pageInfo = new Page<>(page, pageSize);
        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(name != null, Setmeal::getName, name);
        lambdaQueryWrapper.orderByDesc(Setmeal::getUpdateTime);

        setmealService.page(pageInfo, lambdaQueryWrapper);

        /* 添加套餐分类名称：只有categoryId没有categoryName */
        Page<SetmealDto> dtoPageInfo = new Page<>();
        BeanUtils.copyProperties(pageInfo, dtoPageInfo, "records");

        List<Setmeal> setmealList = pageInfo.getRecords();
        List<SetmealDto> setmealDtoList = setmealList.stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item, setmealDto);

            // 对每一行的pageInfo遍历并查询categoryName
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);

            String categoryName = category.getName();
            setmealDto.setCategoryName(categoryName);

            return setmealDto;
        }).collect(Collectors.toList());

        dtoPageInfo.setRecords(setmealDtoList);

        return Result.success(dtoPageInfo);
    }

    /**
     * 根据id（批量）删除
     * @param ids
     * @return
     */
    @DeleteMapping
    public Result<String> delete(@RequestParam List<Long> ids){
        setmealService.removeWithDish(ids);
        return Result.success("删除成功");
    }

    /**
     * 根据setmeal_id进行数据回显
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<SetmealDto> querySetmealById(@PathVariable Long id){
        SetmealDto setmealDto = setmealService.displaySetmealById(id);
        return Result.success(setmealDto);
    }

    /**
     * 修改套餐
     * @param setmealDto
     * @return
     */
    @PutMapping
    public Result<String> editSetmeal(@RequestBody SetmealDto setmealDto){
        setmealService.updateSetmeal(setmealDto);
        return Result.success("修改成功");
    }

    /**
     * 套餐的（批量）起售停售
     * @return
     */
    @PostMapping("/status/{status}")
    public Result<String> changeStatus(@PathVariable Integer status, @RequestParam List<Long> ids){
//        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper = new LambdaQueryWrapper<>();
//        lambdaQueryWrapper.in(Setmeal::getId, ids);
//        List<Setmeal> list = setmealService.list(lambdaQueryWrapper);
//        list = list.stream().map((item) -> {
//            item.setStatus(status);
//            return item;
//        }).collect(Collectors.toList());
//        setmealService.updateBatchById(list);
        LambdaUpdateWrapper<Setmeal> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.in(Setmeal::getId, ids);
        lambdaUpdateWrapper.set(Setmeal::getStatus, status);
        setmealService.update(lambdaUpdateWrapper);
        return Result.success("状态修改成功");
    }



    /**
     * （客户端）套餐查询列表
     * @param setmeal
     * @return
     */
    @GetMapping("/list")
    public Result<List<Setmeal>> list(Setmeal setmeal){
        Long categoryId = setmeal.getCategoryId();
        Integer status = setmeal.getStatus();

        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Setmeal::getCategoryId, categoryId);
        lambdaQueryWrapper.eq(Setmeal::getStatus, status);
        lambdaQueryWrapper.orderByDesc(Setmeal::getUpdateTime);

        List<Setmeal> setmealList = setmealService.list(lambdaQueryWrapper);

        return Result.success(setmealList);
    }

}
