package com.Lijiacheng.controller;

import com.Lijiacheng.common.Result;
import com.Lijiacheng.domain.Category;
import com.Lijiacheng.service.CategoryService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 新增菜品分类
     * */
    @PostMapping
    public Result<String> save(@RequestBody Category category){
        categoryService.save(category);
        return Result.success("新增菜品分类成功");
    }

    /**
     * 分页查询
     * */
    @GetMapping("/page")
    public Result<Page> pageSelect(Integer page, Integer pageSize){
//        Page pageInfo = new Page(page, pageSize);
        Page<Category> pageInfo = new Page<>(page, pageSize);

        // 构建条件构造器
        LambdaQueryWrapper<Category> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.orderByAsc(Category::getSort);

        pageInfo = categoryService.page(pageInfo, lambdaQueryWrapper);
        return Result.success(pageInfo);
    }

    /**
     * 分类管理: 删除操作
     * */
    @DeleteMapping
    public Result<String> delete(@RequestParam("ids") Long categoryId){
        categoryService.remove(categoryId);
        return Result.success("分类信息删除成功");
    }

    /**
     * 根据id修改分类信息
     * */
    @PutMapping
    public Result<String> update(@RequestBody Category category){
        categoryService.updateById(category);
        return Result.success("分类信息修改成功");
    }

    /**
     * 查询菜品分类数据（在菜品管理新增页面回显下拉分类）
     * */
    // 请求参数是固定的(前端处理) type=1
    @GetMapping("/list")
    public Result<List<Category>> displayCategory(Category category){
        LambdaQueryWrapper<Category> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 这里注意在category表中type字段没有非空设置，所以在查询之前一定要记得先进行非空判断
        lambdaQueryWrapper.eq(category.getType() != null, Category::getType, category.getType());
        lambdaQueryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);

        List<Category> categoryList = categoryService.list(lambdaQueryWrapper);
        return Result.success(categoryList);
    }

}
