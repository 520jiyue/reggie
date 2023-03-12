package jiyue.xsl.reggie.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import jiyue.xsl.reggie.Dto.DishDto;
import jiyue.xsl.reggie.Common.CustomException;
import jiyue.xsl.reggie.Entity.Dish;

import java.util.List;

public interface DishService extends IService<Dish>{

    // 新增菜品  同时插入 对应的口味 到 dishflavor
    public void saveWithFlavor(DishDto dishDto);

    // 根据id 查询 对应的菜品和口味
    public DishDto getDishWithFlavor(Long id);

    // 更新
    public void updateDishWithFlavor(DishDto dishDto);

    // 删除
    public void removeDishWithFlavor(List<Long> ids) throws CustomException;


    }
