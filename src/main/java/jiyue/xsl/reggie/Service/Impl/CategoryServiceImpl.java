package jiyue.xsl.reggie.Service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jiyue.xsl.reggie.Common.CustomException;
import jiyue.xsl.reggie.Entity.Category;
import jiyue.xsl.reggie.Entity.Dish;
import jiyue.xsl.reggie.Entity.Setmeal;
import jiyue.xsl.reggie.Mapper.CategoryMapper;
import jiyue.xsl.reggie.Service.CategoryService;
import jiyue.xsl.reggie.Service.DishService;
import jiyue.xsl.reggie.Service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;


    /**
     * 根据id 判断是否 关联菜品 是否可以删除
     * @param ids
     */
    @Override
    public void removeCategory(Long ids) throws CustomException {

        // 添加查询条件， 根据分类id ------> 菜品
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,ids);
        int count = dishService.count(dishLambdaQueryWrapper);

        // 添加查询条件， 根据分类id ------> 套餐
        LambdaQueryWrapper<Setmeal> dishLambdaQueryWrapper0 = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper0.eq(Setmeal::getCategoryId,ids);
        int count0 = setmealService.count(dishLambdaQueryWrapper0);


        // 1. 查询当前分类是否有关联其他菜品  yes ： 抛出异常
        if (count > 0) {
            // 已关联菜品 抛出异常
            throw new CustomException("当前分类关联了菜品 ！！！ 无法删除 ！！！ ");

        }
        // 2. 查询当前分类是否有关联其他套餐  yes ： 抛出异常
        if (count0 > 0) {
            // 已关联套餐 抛出异常
            throw new CustomException("当前分类关联了其他套餐 ！！！ 无法删除 ！！！");

        }
        // 3. 正常删除

        super.removeById(ids);


    }
}
