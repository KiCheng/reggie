package com.Lijiacheng.dto;

import com.Lijiacheng.domain.Dish;
import com.Lijiacheng.domain.DishFlavor;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;
@Data
public class DishDto extends Dish {

    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}
