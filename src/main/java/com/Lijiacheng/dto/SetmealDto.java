package com.Lijiacheng.dto;


import com.Lijiacheng.domain.Setmeal;
import com.Lijiacheng.domain.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
