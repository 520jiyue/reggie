package jiyue.xsl.reggie.Service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jiyue.xsl.reggie.Dto.DishDto;
import jiyue.xsl.reggie.Common.CustomException;
import jiyue.xsl.reggie.Entity.Dish;
import jiyue.xsl.reggie.Entity.DishFlavor;
import jiyue.xsl.reggie.Mapper.DishMapper;
import jiyue.xsl.reggie.Service.DishFlavorService;
import jiyue.xsl.reggie.Service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;



@Service
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService{

    @Autowired
    private DishFlavorService dishFlavorService;
    /**
     * 新增菜品  同时插入 对应的口味 到 dishflavor
     * @param dishDto
     */
    @Override
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        
        // 保存基本 dish
        this.save(dishDto);

        // 处理 dish——id
        Long dishid = dishDto.getId();
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishid);
            return item;
        }).collect(Collectors.toList());

        // 保存到flavor
        dishFlavorService.saveBatch(flavors);
    }

    @Override
    public DishDto getDishWithFlavor(Long id) {

        // 查询基本信息
        Dish dish = this.getById(id);

        DishDto dishDto = new DishDto();

        BeanUtils.copyProperties(dish,dishDto);

        // 查询口味信息
        LambdaQueryWrapper<DishFlavor> query = new LambdaQueryWrapper<DishFlavor>();
        query.eq(DishFlavor::getDishId,dish.getId());
        List<DishFlavor> flavors = dishFlavorService.list(query);

        dishDto.setFlavors(flavors);
        return dishDto;
    }


    @Override
    @Transactional
    public void updateDishWithFlavor(DishDto dishDto) {

        // 更新dish
        this.updateById(dishDto);

        // 更新口味  删除原来的  在更新
        LambdaQueryWrapper<DishFlavor> query = new LambdaQueryWrapper<DishFlavor>();
        query.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(query);

        // 更新口味

        List<DishFlavor> flavors = dishDto.getFlavors();

        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);

    }

    @Override
    @Transactional
    public void removeDishWithFlavor(List<Long> ids) throws CustomException {

        // 删除 dish
        LambdaQueryWrapper<Dish> query = new LambdaQueryWrapper<>();
        query.in(ids != null, Dish::getId, ids);
        List<Dish> dish_list = this.list(query);
        // 拿到 id 数据 dish的 list
        for (Dish dish : dish_list) {
            Integer status = dish.getStatus();
            if (status == 0) {
                // 判断物品是否再出售
                this.removeById(dish.getId());
                // 删除 口味 信息
                LambdaQueryWrapper<DishFlavor> query_flavor = new LambdaQueryWrapper();
                query_flavor.eq(DishFlavor::getDishId, dish.getId());
                dishFlavorService.remove(query_flavor);

            }else {throw new CustomException("删除菜品正在售卖，无法删除");}
        }

        // 删除 口味 信息
//        LambdaQueryWrapper<DishFlavor> query_flavor = new LambdaQueryWrapper();
//        for (Long id:ids) {
//            query_flavor.eq(DishFlavor::getDishId, id);
//            dishFlavorService.remove(query_flavor);
//        }
    }
}
