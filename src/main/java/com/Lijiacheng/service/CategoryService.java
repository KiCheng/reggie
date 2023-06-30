package com.Lijiacheng.service;

import com.Lijiacheng.domain.Category;
import com.baomidou.mybatisplus.extension.service.IService;

public interface CategoryService extends IService<Category> {
    public void remove(Long categoryId);
}
