package com.Lijiacheng.service;

import com.Lijiacheng.domain.Setmeal;
import com.Lijiacheng.dto.SetmealDto;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    public void saveWithDish(SetmealDto setmealDto);

    public void removeWithDish(List<Long> ids);
}
