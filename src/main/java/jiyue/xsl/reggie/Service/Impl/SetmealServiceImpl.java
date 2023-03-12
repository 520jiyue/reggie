package jiyue.xsl.reggie.Service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jiyue.xsl.reggie.Dto.SetmealDto;
import jiyue.xsl.reggie.Common.CustomException;
import jiyue.xsl.reggie.Entity.Setmeal;
import jiyue.xsl.reggie.Entity.SetmealDish;
import jiyue.xsl.reggie.Mapper.SetmealMapper;
import jiyue.xsl.reggie.Service.SetmealDishService;
import jiyue.xsl.reggie.Service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {


    @Autowired
    private SetmealDishService setmealDishService;

    /**
     * 新增套餐 setmeal and setmeal_dish
     * @param setmealDto
     */
    @Override
    public void setwithDishsave(SetmealDto setmealDto) {

        // 保存套餐基本信息  setmeal
        this.save(setmealDto);

        // 保存套餐和菜品关联  setmeal_dish
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();

        // 将  setmealDto.getId  ----》  SetmealId
        setmealDishes = setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        setmealDishService.saveBatch(setmealDishes);
    }


    /**
     * 修改数据  先做查询
     * @param id
     */
    @Override
    public SetmealDto setSetmeal(Long id) {

        //  查询基本信息
        Setmeal setmeal = this.getById(id);

        SetmealDto setmealDto = new SetmealDto();

        BeanUtils.copyProperties(setmeal, setmealDto);

        // 查询 菜品
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,setmeal.getId());
        List<SetmealDish> mealDishes = setmealDishService.list(queryWrapper);

        setmealDto.setSetmealDishes(mealDishes);
        return setmealDto;
    }


    @Transactional
    @Override
    public void setmealupdate(SetmealDto setmealDto) {

        // 更新 setmeal
        this.updateById(setmealDto);

        // 删除原来的菜品信息
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,setmealDto.getId());
        setmealDishService.remove(queryWrapper);


        // 更新口味
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        // 将  setmealDto.getId  ----》  SetmealId

        setmealDishes = setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        setmealDishService.saveBatch(setmealDishes);

    }

    @Override
    public void deletSetmeal(List<Long> ids) throws CustomException {

        // 删除 setmeal
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in( ids != null , Setmeal::getId, ids);
        List<Setmeal> setmealList = this.list(queryWrapper);

        // 拿到 setmeal 中 数据 对应 setmeal dish id
        for (Setmeal setmeal : setmealList) {
            Integer status = setmeal.getStatus();
            if (status == 0){
                this.removeById(setmeal.getId());
                // 删除口味信息
                LambdaQueryWrapper<SetmealDish> query = new LambdaQueryWrapper<>();
                query.eq(SetmealDish::getSetmealId,setmeal.getId());
                setmealDishService.remove(query);
            }else {
                throw new CustomException("有套餐正在出售 无法删除");
            }
        }
    }
}
