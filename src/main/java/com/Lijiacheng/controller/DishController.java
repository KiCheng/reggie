package com.Lijiacheng.controller;

import com.Lijiacheng.common.Result;
import com.Lijiacheng.domain.Category;
import com.Lijiacheng.domain.Dish;
import com.Lijiacheng.domain.DishFlavor;
import com.Lijiacheng.dto.DishDto;
import com.Lijiacheng.service.CategoryService;
import com.Lijiacheng.service.DishFlavorService;
import com.Lijiacheng.service.DishService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜品管理
 */
@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private DishFlavorService dishFlavorService;

    /**
     * 菜品管理:新增菜品
     *
     * @param dishDto
     * @return
     */
    @PostMapping
    public Result<String> save(@RequestBody DishDto dishDto) {
        log.info(dishDto.toString());
        /*
         * 新增菜品，同时添加菜品对应的口味数据 -- 要操作两张数据表，所以要自己设计业务层方法
         * */
        dishService.saveWithFlavor(dishDto);
        return Result.success("新增菜品成功");

    }

    /**
     * 菜品管理的分页查询
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public Result<Page> pageSelect(int page, int pageSize, String name) {
        Page<Dish> pageInfo = new Page<>(page, pageSize);
        Page<DishDto> dishDtoPage = new Page<>();

        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(name != null, Dish::getName, name);
        lambdaQueryWrapper.orderByDesc(Dish::getUpdateTime);

        dishService.page(pageInfo, lambdaQueryWrapper);

        // 对象拷贝pageInfo
        BeanUtils.copyProperties(pageInfo, dishDtoPage, "records");

/**
         List<DishDto> dishDtoRecords = dishDtoPage.getRecords();  // 赋值时size=0
         // 遍历pageInfo的每一行数据
        for(Dish dishRecord: pageInfo.getRecords()){
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(dishRecord, dishDto);
            // 查询数据库category_id对应的name
            Category category = categoryService.getById(dishRecord.getCategoryId());
            if(category != null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
                dishDtoRecords.add(dishDto); // 有点问题???????????
                log.info(dishDto.toString());
            }
        }
 */
        List<Dish> records = pageInfo.getRecords();
        List<DishDto> dishDtoList = records.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);

            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            String categoryName = category.getName();
            dishDto.setCategoryName(categoryName);

            return dishDto;
        }).collect(Collectors.toList());

        dishDtoPage.setRecords(dishDtoList);
        return Result.success(dishDtoPage);
    }

    /**
     * 根据id在菜品中回显数据
     * @param dishId
     * @return
     */
    @GetMapping("/{id}")
    public Result<DishDto> displayDish(@PathVariable("id") Long dishId){

        DishDto dishDto = dishService.getByIdWithFlavor(dishId);
        return Result.success(dishDto);
    }

    /**
     * 修改菜品信息
     * @param dishDto
     * @return
     */
    @PutMapping
    public Result<String> update(@RequestBody DishDto dishDto){

        dishService.updateWithFlavor(dishDto);
        return Result.success("修改菜品成功");
    }

    /**
     * 根据菜品分类id查询菜品分类信息
     * @param dish
     * @return
     */
    /*
    @GetMapping("/list")
    public Result<List<Dish>> list(Dish dish){
        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        Long categoryId = dish.getCategoryId();

        lambdaQueryWrapper.eq(categoryId != null, Dish::getCategoryId, categoryId);
        lambdaQueryWrapper.eq(Dish::getStatus, 1);  // 状态是"起售"的菜品
        lambdaQueryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getCategoryId);

        List<Dish> dishList = dishService.list(lambdaQueryWrapper);

        return Result.success(dishList);
    }
     */
    @GetMapping("/list")
    public Result<List<DishDto>> list(Dish dish){
        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        Long categoryId = dish.getCategoryId();

        lambdaQueryWrapper.eq(categoryId != null, Dish::getCategoryId, categoryId);
        lambdaQueryWrapper.eq(Dish::getStatus, 1);  // 状态是"起售"的菜品
        lambdaQueryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getCategoryId);

        List<Dish> dishList = dishService.list(lambdaQueryWrapper);

        // 将dishList拷贝给dishDtoList
        List<DishDto> dishDtoList = dishList.stream().map((item) -> {
            // item是具体的菜品
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            // 获取菜品dish_id
            Long dishId = item.getId();
            LambdaQueryWrapper<DishFlavor> flavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
            flavorLambdaQueryWrapper.eq(DishFlavor::getDishId, dishId);

            List<DishFlavor> dishFlavorList = dishFlavorService.list(flavorLambdaQueryWrapper);
            dishDto.setFlavors(dishFlavorList);
            return dishDto;
        }).collect(Collectors.toList());

        return Result.success(dishDtoList);
    }


    /**
     * 菜品的（批量）起售停售
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    public Result<String> changeStatus(@PathVariable Integer status, @RequestParam("ids") Long[] id){
//        log.info("status --> {}", status);
//        log.info("ids --> {}", id);

        LambdaUpdateWrapper<Dish> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.in(Dish::getId, id);
        lambdaUpdateWrapper.set(Dish::getStatus, status);
        dishService.update(lambdaUpdateWrapper);

        return Result.success("状态更改成功");
    }

    /**
     * 删除菜品
     * @param ids
     * @return
     */
    @DeleteMapping
    public Result<String> delete(Long[] ids){

        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(Dish::getId, ids);
        dishService.remove(lambdaQueryWrapper);

        return Result.success("删除成功");
    }



}
