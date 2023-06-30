package com.Lijiacheng.service;

import com.Lijiacheng.domain.Dish;
import com.Lijiacheng.dto.DishDto;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.stereotype.Service;

public interface DishService extends IService<Dish> {
    public void saveWithFlavor(DishDto dishDto);

    public DishDto getByIdWithFlavor(Long id);

    public void updateWithFlavor(DishDto dishDto);
}
